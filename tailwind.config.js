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
  darkMode: 'class',
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
          50: '#EBF5FF',
          100: '#E1EFFE',
          200: '#C3DDFD',
          300: '#A4CAFE',
          400: '#76A9FA',
          500: '#3F83F8',
          600: '#1C64F2',
          700: '#1A56DB',
          800: '#1E429F',
          900: '#233876',
        },
      },
    },
  },
  plugins: [require('@tailwindcss/forms')],
}
