import React from 'react'
import { useNavigate } from 'react-router-dom';
import { useAppContext } from '../context/AppContext';

const BlogCard = ({blog}) => {
    const {title, subTitle, category, description, id, image} = blog;
    const {navigate} = useAppContext()
  return (
    <div onClick={() => navigate(`/blogs/${id}`)} className='w-full rounded-lg overflow-hidden shadow hover:scale-102 hover:shadow-primary/25 duration-300 cursor-pointer'>
      <img src={image} alt="" className='aspect-video'/>
      <span className='ml-5 mt-4 px-3 py-1
      inline-block bg-primary/20 rounded-full'>{category}</span>
      <div className='p-5'>
        <h5 className='mb-2 font-medium text-gray-900'>{title}</h5>
        <h5 className='mb-3 font-medium text-gray-600'>{subTitle}</h5>
        <p className='mb-3 text-xs text-gray-600' dangerouslySetInnerHTML={{"__html" : description.slice(0, 80)}}></p>
      </div>
    </div>
  )
}

export default BlogCard
