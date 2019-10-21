module.exports = {
  transform: {
    '.(ts|tsx)': 'ts-jest',
  },
  transformIgnorePatterns: ['[/\\\\]node_modules[/\\\\].+\\.(js|jsx)$'],
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  collectCoverageFrom: ['src/**/*.{ts,tsx}'],
  testMatch: ['**/*.(spec|test).{ts,tsx}'],
  testURL: 'http://localhost',
  modulePaths: ['<rootDir>/src/', '<rootDir>/node_modules/'],
  coveragePathIgnorePatterns: ['/node_modules/', '/test/'],
};
