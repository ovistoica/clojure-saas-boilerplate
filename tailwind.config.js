const defaultTheme = require('tailwindcss/defaultTheme')

/** @type {import('tailwindcss').Config} */
module.exports = {
  // in prod look at shadow-cljs output file in dev look at runtime, which will change files that are actually compiled; postcss watch should be a whole lot faster
  content:
    process.env.NODE_ENV === 'production'
      ? ['./resources/public/assets/js/main.js']
      : [
          './resources/public/assets/js/cljs-runtime/*.js',
          './out/devcards/js/cljs-runtime/*_cards.js',
          './resources/css/devcards-tailwind.css',
        ],
  theme: {
    extend: {
      borderWidth: {
        1: '1px',
        16: '1em',
        24: '1.5em',
      },
      minWidth: {
        1: '0.5em',
      },
      fontFamily: {
        sans: ['Inter var', ...defaultTheme.fontFamily.sans],
      },
      colors: {
        // Brand is tailwind cyan
        brand: {
          50: '#ecfeff',
          100: '#cffafe',
          200: '#a5f3fc',
          300: '#67e8f9',
          400: '#22d3ee',
          500: '#06b6d4',
          600: '#0891b2',
          700: '#0e7490',
          800: '#155e75',
          900: '#164e63',
        },
      },
    },
  },
  plugins: [require('@tailwindcss/forms')],
}
