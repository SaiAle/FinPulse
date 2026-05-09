/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: { extend: {
    colors: {
      fin: {
        green:  '#00C897',
        red:    '#FF4560',
        blue:   '#008FFB',
        purple: '#775DD0',
        bg:     '#0A0E1A',
        card:   '#111827',
        border: '#1F2937',
      }
    }
  }},
  plugins: [],
};
