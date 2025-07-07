/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}", // This tells Tailwind to scan all your component files
  ],
  theme: {
    extend: {
      // You can add your custom fonts here if you did before
      fontFamily: {
        sans: ['Sora', 'sans-serif'],
      },
    },
  },
  plugins: [],
}