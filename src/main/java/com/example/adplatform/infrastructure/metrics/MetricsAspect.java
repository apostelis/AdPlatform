package com.example.adplatform.infrastructure.metrics;

import com.example.adplatform.application.exception.AdvertisementNotFoundException;
import com.example.adplatform.application.exception.AdvertisementValidationException;
import com.example.adplatform.domain.model.Advertisement;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Cross-cutting metrics for application services using Spring AOP.
 * This aspect encapsulates Micrometer instrumentation to keep service methods clean.
 */
@Aspect
@Component
@Order(10) // ensure metrics run after security/transactions defaults
public class MetricsAspect {

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    private MeterRegistry getRegistry() {
        return meterRegistry != null ? meterRegistry : Metrics.globalRegistry;
    }

    /* =========================
     *  Pointcuts
     * ========================= */

    @Pointcut("execution(* com.example.adplatform.application.service.AdvertisementServiceImpl.getAdvertisementByIdOrThrow(..))")
    public void getByIdOrThrow() {}

    @Pointcut("execution(* com.example.adplatform.application.service.AdvertisementServiceImpl.saveAdvertisement(..))")
    public void saveAdvertisement() {}

    @Pointcut("execution(* com.example.adplatform.application.service.AdvertisementServiceImpl.getTargetedAdvertisements(..))")
    public void combinedTargeting() {}

    @Pointcut("execution(* com.example.adplatform.application.service.AdvertisementServiceImpl.getGeoTargetedAdvertisements(..))")
    public void geoTargeting() {}

    @Pointcut("execution(* com.example.adplatform.application.service.AdvertisementServiceImpl.getBioTargetedAdvertisements(..))")
    public void bioTargeting() {}

    @Pointcut("execution(* com.example.adplatform.application.service.AdvertisementServiceImpl.getMoodTargetedAdvertisements(..))")
    public void moodTargeting() {}

    /* =========================
     *  Advices
     * ========================= */

    @Around("getByIdOrThrow()")
    public Object timeGetByIdOrThrow(ProceedingJoinPoint pjp) throws Throwable {
        Timer.Sample sample = Timer.start(getRegistry());
        try {
            Object result = pjp.proceed();
            sample.stop(Timer.builder("advertisements.fetch.byId")
                    .tags(Tags.of("status", "success"))
                    .register(getRegistry()));
            return result;
        } catch (AdvertisementNotFoundException ex) {
            sample.stop(Timer.builder("advertisements.fetch.byId")
                    .tags(Tags.of("status", "not_found"))
                    .register(getRegistry()));
            throw ex;
        } catch (RuntimeException ex) {
            sample.stop(Timer.builder("advertisements.fetch.byId")
                    .tags(Tags.of("status", "error"))
                    .register(getRegistry()));
            throw ex;
        }
    }

    @Around("saveAdvertisement()")
    public Object countSaves(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String operation = "unknown";
        if (args != null && args.length > 0 && args[0] instanceof Advertisement ad) {
            operation = (ad.getId() == null) ? "create" : "update";
        }
        try {
            Object result = pjp.proceed();
            incrementSaveCounter(operation, "success");
            return result;
        } catch (AdvertisementValidationException ave) {
            incrementSaveCounter(operation, "validation_error");
            try {
                int errorCount = 1;
                Map<String, String> errors = ave.getErrors();
                if (errors != null && !errors.isEmpty()) {
                    errorCount = errors.size();
                }
                incrementValidationErrors(errorCount);
            } catch (Exception ignore) {
                // do not interfere with main flow if metrics collection fails
            }
            throw ave;
        } catch (Throwable t) {
            // for any other failure (including wrapped AdvertisementOperationException)
            incrementSaveCounter(operation, "failure");
            throw t;
        }
    }

    @Around("combinedTargeting()")
    public Object timeCombinedTargeting(ProceedingJoinPoint pjp) throws Throwable {
        return timeTargetingWithStrategy(pjp, "combined");
    }

    @Around("geoTargeting()")
    public Object timeGeoTargeting(ProceedingJoinPoint pjp) throws Throwable {
        return timeTargetingWithStrategy(pjp, "geo");
    }

    @Around("bioTargeting()")
    public Object timeBioTargeting(ProceedingJoinPoint pjp) throws Throwable {
        return timeTargetingWithStrategy(pjp, "bio");
    }

    @Around("moodTargeting()")
    public Object timeMoodTargeting(ProceedingJoinPoint pjp) throws Throwable {
        return timeTargetingWithStrategy(pjp, "mood");
    }

    private Object timeTargetingWithStrategy(ProceedingJoinPoint pjp, String strategy) throws Throwable {
        Timer.Sample sample = Timer.start(getRegistry());
        try {
            Object result = pjp.proceed();
            sample.stop(Timer.builder("advertisements.targeting.compute")
                    .tags(Tags.of("strategy", strategy, "status", "success"))
                    .register(getRegistry()));
            return result;
        } catch (Throwable t) {
            sample.stop(Timer.builder("advertisements.targeting.compute")
                    .tags(Tags.of("strategy", strategy, "status", "error"))
                    .register(getRegistry()));
            throw t;
        }
    }

    private void incrementSaveCounter(String operation, String status) {
        Counter.builder("advertisements.saved")
                .tags(Tags.of("operation", operation, "status", status))
                .register(getRegistry())
                .increment();
    }

    private void incrementValidationErrors(int count) {
        Counter.builder("advertisements.validation.errors")
                .register(getRegistry())
                .increment(count);
    }
}
