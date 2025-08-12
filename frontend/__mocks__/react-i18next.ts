const dict: Record<string, string> = {
  'field.title': 'Title',
  'field.description': 'Description',
  'field.content': 'Content',
  'field.source': 'Source',
  'field.storage': 'Storage',
  'field.youtube': 'YouTube',
  'field.sourceIdentifier.youtube': 'Source Identifier (YouTube)',
  'field.sourceIdentifier.storage': 'Source Identifier (Storage)',
  'form.basicInformation': 'Basic Information',
  'form.clickBehavior': 'Click Behavior',
  'form.geoTargeting': 'Geo Targeting',
  'form.bioTargeting': 'Bio Targeting',
  'form.moodTargeting': 'Mood Targeting',
  'field.active': 'Active',
  'field.clickable': 'Clickable',
  'field.targetUrl': 'Target URL',
};

export const useTranslation = () => ({
  t: (key: string, opts?: any) => dict[key] || (opts && opts.defaultValue) || key,
  i18n: { changeLanguage: jest.fn() },
});
export const Trans = ({ children }: any) => children;
export default { useTranslation, Trans };
