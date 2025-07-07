import React from 'react';

type InputProps = React.InputHTMLAttributes<HTMLInputElement>;

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type, ...props }, ref) => {
    return (
      <input
        type={type}
        className={`w-full px-4 py-3 bg-gray-800/50 border border-white/10 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-300 ${className}`}
        ref={ref}
        {...props}
      />
    );
  }
);