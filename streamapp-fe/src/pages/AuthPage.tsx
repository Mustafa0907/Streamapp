import React, { useState } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { useForm, type SubmitHandler } from 'react-hook-form';
import { useAuthStore } from '../store/authStore';
import api from '../lib/api';
import { Input } from '../components/ui/Input';
import { Button } from '../components/ui/Button';
import { motion } from 'framer-motion';
import { Loader2, RadioTower } from 'lucide-react';

// Define the shape of our form data for TypeScript
type FormValues = {
  username: string;
  password: string;
  email?: string;
};

const AuthPage: React.FC = () => {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const setAuth = useAuthStore((state) => state.setAuth);
  const isLogin = pathname === '/login';

  const { register, handleSubmit, formState: { errors } } = useForm<FormValues>();
  const [isLoading, setIsLoading] = useState(false);
  const [apiError, setApiError] = useState<string | null>(null);

  const onSubmit: SubmitHandler<FormValues> = async (data) => {
    setIsLoading(true);
    setApiError(null);
    try {
      if (isLogin) {
        const response = await api.post('/api/auth/login', { username: data.username, password: data.password });
        setAuth(response.data.token, response.data);
        navigate('/');
      } else {
        await api.post('/api/auth/register', data);
        alert('Registration successful! Please proceed to login.');
        navigate('/login');
      }
    } catch (err: any) {
      setApiError(err.response?.data?.message || err.response?.data || 'An unknown error occurred.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="relative flex items-center justify-center min-h-screen w-full overflow-hidden bg-[#0D1117] p-4 font-sans">
      {/* Dynamic Aurora Background */}
      <div className="absolute inset-0 z-0">
        <div className="absolute top-0 left-0 -translate-x-1/4 -translate-y-1/3 w-[70vw] h-[70vh] max-w-[800px] max-h-[800px] rounded-full bg-blue-600/20 blur-3xl animate-[spin_20s_linear_infinite]" />
        <div className="absolute bottom-0 right-0 translate-x-1/3 translate-y-1/3 w-[60vw] h-[60vh] max-w-[600px] max-h-[600px] rounded-full bg-purple-600/20 blur-3xl animate-[spin_25s_linear_infinite_reverse]" />
      </div>
      
      {/* Animated Form Container with Glassmorphism */}
      <motion.div 
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.5, ease: "easeOut" }}
        className="relative z-10 w-full max-w-sm p-8 space-y-6 bg-gray-900/60 backdrop-blur-lg border border-white/10 rounded-2xl shadow-2xl"
      >
        <div className="flex flex-col items-center text-center">
            <div className="p-3 bg-blue-600/20 border border-blue-500/30 rounded-full mb-4">
                <RadioTower className="text-blue-400" size={28} />
            </div>
          <h1 className="text-4xl font-bold text-white tracking-tight">
            {isLogin ? 'Welcome Back' : 'Create Your Account'}
          </h1>
          <p className="text-gray-400 mt-2">
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <Link to={isLogin ? '/register' : '/login'} className="font-medium text-blue-400 hover:text-blue-300 transition-colors">
              {isLogin ? 'Sign Up' : 'Sign In'}
            </Link>
          </p>
        </div>
        
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {!isLogin && (
            <div>
              <Input aria-label="Email" placeholder="Email" {...register('email', { required: 'Email is required', pattern: { value: /^\S+@\S+$/i, message: 'Invalid email address' } })} />
              {errors.email && <p className="text-red-400 text-xs mt-1 px-1">{String(errors.email.message)}</p>}
            </div>
          )}
          <div>
            <Input aria-label="Username" placeholder="Username" {...register('username', { required: 'Username is required' })} />
            {errors.username && <p className="text-red-400 text-xs mt-1 px-1">{String(errors.username.message)}</p>}
          </div>
          <div>
            <Input aria-label="Password" type="password" placeholder="Password" {...register('password', { required: 'Password is required' })} />
            {errors.password && <p className="text-red-400 text-xs mt-1 px-1">{String(errors.password.message)}</p>}
          </div>
          {apiError && <p className="text-red-400 text-sm text-center bg-red-500/10 p-2 rounded-md">{String(apiError)}</p>}
          <Button type="submit" disabled={isLoading} className="w-full !mt-6">
            {isLoading ? <Loader2 className="animate-spin" /> : (isLogin ? 'Sign In' : 'Create Account')}
          </Button>
        </form>
      </motion.div>
    </div>
  );
};

export default AuthPage;