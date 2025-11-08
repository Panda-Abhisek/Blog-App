/* eslint-disable no-constant-condition */
import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Blog from './pages/Blog'
import Home from './pages/Home'
import Layout from './pages/admin/Layout'
import Dashboard from './pages/admin/Dashboard'
import AddBlog from './pages/admin/AddBlog'
import ListBlog from './pages/admin/ListBlog'
import Comments from './pages/admin/Comments'
import Login from './components/admin/Login'
import 'quill/dist/quill.snow.css'
import { Toaster } from 'react-hot-toast'
import { useAppContext } from './context/AppContext'
import Register from './components/admin/Register'
import OAuth2RedirectHandler from './components/admin/OAuth2RedirectHandler'
import ForgotPassword from './components/admin/ForgotPassword'
import ResetPassword from './components/admin/ResetPassword'

const App = () => {

  const {user} = useAppContext()
  console.log(user);

  return (
    <div>
      <Toaster />
      <Routes>
        <Route path='/' element={<Home/>} />
        <Route path='/blogs/:id' element={<Blog/>} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        {/* <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} /> */}
        <Route path="/forgot-password" element={<ForgotPassword />} />
        
        <Route path='/admin' element={user ? <Layout /> : <Login />}>
          <Route index element={<Dashboard/>}/>
          <Route path='addBlog' element={<AddBlog/>}/>
          <Route path='listBlog' element={<ListBlog/>}/>
          <Route path='comments' element={<Comments/>}/>
        </Route>
        <Route path="/reset-password" element={<ResetPassword />} />
      </Routes>
    </div>
  )
}

export default App
