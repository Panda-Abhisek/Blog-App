/* eslint-disable no-unused-vars */
import React, { useState } from "react";
import { assets } from "../../assets/assets";
import { useAppContext } from "../../context/AppContext";
import toast from "react-hot-toast";

const BlogTableItem = ({ blog, fetchBlogs, index }) => {
  // console.log(blog);
  const { axios } = useAppContext();
  const { title, createdAt, published, id } = blog;
  // console.log(published);
  const BlogDate = new Date(createdAt);
  const [showPopup, setShowPopup] = useState(false);

  const togglePublish = async () => {
    try {
      await axios.put(
        `/api/blogs/${id}/publish`,
        { published: !published }
      );
      await fetchBlogs(); // refresh table data
    } catch (error) {
      toast.error("Failed to toggle publish state:", error);
    }
  };

  const deleteBlog = async () => {
    try {
      await axios.delete(`/api/blogs/${id}`);
      await fetchBlogs(); // refresh the list
    } catch (error) {
      toast.error("Failed to delete blog:", error);
    } finally {
      setShowPopup(false); // close popup
    }
  };

  return (
    <>
      <tr className="border-y border-gray-300">
        <th className="px-2 py-4">{index}</th>
        <td className="px-2 py-4">{title}</td>
        <td className="px-2 py-4 max-sm:hidden">{BlogDate.toDateString()}</td>
        <td className="px-2 py-4 max-sm:hidden">
          <p
            className={`${
              blog.published ? "text-green-600" : "text-orange-700"
            }`}
          >
            {blog.published ? "Published" : "Unpublished"}
          </p>
        </td>
        <td className="px-2 py-4 flex text-xs gap-3">
          <button
            onClick={togglePublish}
            className="border hover:scale-102 transition-all px-2 py-0.5 mt-1 rounded cursor-pointer"
          >
            {blog.published ? "Unpublish" : "Publish"}
          </button>
          <img
            src={assets.cross_icon}
            onClick={() => setShowPopup(true)}
            className="w-8 hover:scale-110 transition-all cursor-pointer"
            alt="Delete"
          />
        </td>
      </tr>

      {showPopup && (
        <div className="fixed inset-0 bg-gray-600/30 bg-opacity-40 flex justify-center items-center z-50">
          <div className="bg-white rounded-xl shadow-lg p-6 w-80 text-center">
            <h2 className="text-lg font-semibold mb-3">Delete Blog</h2>
            <p className="text-gray-600 mb-5">
              Are you sure you want to delete this blog permanently?
            </p>
            <div className="flex justify-center gap-4">
              <button
                onClick={deleteBlog}
                className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
              >
                Delete
              </button>
              <button
                onClick={() => setShowPopup(false)}
                className="border border-gray-400 px-4 py-2 rounded hover:bg-gray-100"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default BlogTableItem;
