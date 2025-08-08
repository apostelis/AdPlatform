"use client";

import React from "react";
import { Alert, AlertTitle, Button, Box } from "@mui/material";

interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
}

export default class ErrorBoundary extends React.Component<React.PropsWithChildren, ErrorBoundaryState> {
  constructor(props: React.PropsWithChildren) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo): void {
    // In a real app, send to logging/observability service
    // eslint-disable-next-line no-console
    console.error("Unhandled UI error:", error, errorInfo);
  }

  handleReload = () => {
    if (typeof window !== 'undefined') {
      window.location.reload();
    }
  };

  render() {
    if (this.state.hasError) {
      return (
        <Box sx={{ m: 2 }}>
          <Alert severity="error">
            <AlertTitle>Something went wrong</AlertTitle>
            An unexpected error occurred while rendering this page. You can try reloading.
            <Box sx={{ mt: 2 }}>
              <Button variant="contained" color="error" onClick={this.handleReload}>Reload</Button>
            </Box>
          </Alert>
        </Box>
      );
    }

    return this.props.children;
  }
}
