import React from 'react'
import { assets } from '../assets/assets'
import { useAppContext } from '../context/AppContext';
import { Link } from 'react-router-dom';

const Navbar = () => {
  const { navigate, user } = useAppContext();

  return (
    <div className='flex justify-between items-center py-5 mx-8 sm:mx-20 xl:mx-32'>
      {/* Logo */}
      <img 
        onClick={() => navigate('/')} 
        src={assets.logo} 
        alt="logo" 
        className='w-32 sm:w-44 cursor-pointer'
      />

      <div className='flex items-center gap-4'>
        {/* External GitHub Link - Changed to <a> tag */}
        <a 
          href='https://github.com/Panda-Abhisek/Blog-App' 
          target="_blank" 
          rel="noopener noreferrer"
          className='flex items-center gap-2 rounded-full text-sm cursor-pointer bg-gray-500 text-white px-10 py-2.5 hover:scale-105 transition-all'
        >
          github
          <img src={assets.arrow} className='w-3' alt="arrow" />
        </a>

        {/* Admin/Login Navigation - Use Link for better SEO/Accessibility */}
        <Link 
          to='/admin' 
          className='flex items-center gap-2 rounded-full text-sm cursor-pointer bg-primary text-white px-10 py-2.5 hover:scale-105 transition-all'
        >
          {user ? 'Dashboard' : 'Login'}
          <img src={assets.arrow} className='w-3' alt="arrow" />
        </Link>
      </div>
    </div>
  )
}

export default Navbar
