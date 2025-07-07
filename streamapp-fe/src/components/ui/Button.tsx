import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
  variant?: 'primary' | 'secondary' | 'danger';
}

export const Button = ({ className, children, variant = 'primary', ...props }: ButtonProps) => {
  const baseClasses = "px-6 py-3 font-semibold rounded-lg focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-900 transition-all duration-300 transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2";
  
  const variantClasses = {
    primary: 'bg-blue-600 hover:bg-blue-500 text-white focus:ring-blue-400',
    secondary: 'bg-gray-700 hover:bg-gray-600 text-gray-200 focus:ring-gray-500',
    danger: 'bg-red-600 hover:bg-red-500 text-white focus:ring-red-400',
  };

  return (
    <button
      className={`${baseClasses} ${variantClasses[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};