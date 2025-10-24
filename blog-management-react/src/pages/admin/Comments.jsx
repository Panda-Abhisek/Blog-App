/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { comments_data } from "../../assets/assets";
import CommentTableItem from "../../components/admin/CommentTableItem";
import { useAppContext } from "../../context/AppContext";

const Comments = () => {
  const [comments, setComments] = useState([]);
  const [filter, setFilter] = useState("Not Approved");
  const { axios, user } = useAppContext();
  const [blogs, setBlogs] = useState([]);

  // const fetchComments = async () => {
  //   try {
  //     const userId = user?.id; // however you're storing it
  //     const response = await axios.get(`/api/dashboard/listblog`, {
  //       params: { userId: userId },
  //     });

  //     const blogsData = response.data;
  //     console.log(blogsData);
  //     setBlogs(blogsData);

  //     if (blogsData.length > 0) {
  //       const commentResponse = await axios.get(
  //         `/api/comments/blog/${blogsData[0].id}`
  //       );
  //       console.log(commentResponse);

  //       setComments(commentResponse.data);
  //     }
  //   } catch (error) {
  //     console.error("Error fetching data:", error);
  //   }
  // };

  const fetchComments = async () => {
    try {
      const userId = user?.id;
      const { data: blogsData } = await axios.get(`/api/dashboard/listblog`, {
        params: { userId },
      });

      setBlogs(blogsData);

      if (blogsData.length === 0) return;

      // Fetch comments for all blogs in parallel
      const commentPromises = blogsData.map((blog) =>
        axios.get(`/api/comments/blog`, {
          params: {blogId : blog.id},
        })
      );

      const commentsResponses = await Promise.all(commentPromises);

      // Flatten all comment arrays into one
      const allComments = commentsResponses.flatMap((res) => res.data);

      setComments(allComments);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  console.log(comments);

  useEffect(() => {
    fetchComments();
  }, [user]);

  return (
    <div className="flex-1 pt-5 px-5 sm:pt-12 sm:pl-16 bg-blue-50/50">
      <div className="flex justify-between items-center max-w-3xl">
        <h1>Comments</h1>
        <div className="flex gap-4">
          <button
            onClick={() => setFilter("Approved")}
            className={`shadow-custom-sm border rounded-full px-4 py-1 cursor-pointer text-xs ${
              filter === "Approved" ? "text-primary" : "text-gray-700"
            }`}
          >
            Approved
          </button>

          <button
            onClick={() => setFilter("Not Approved")}
            className={`shadow-custom-sm border rounded-full px-4 py-1 cursor-pointer text-xs ${
              filter === "Not Approved" ? "text-primary" : "text-gray-700"
            }`}
          >
            Not Approved
          </button>
        </div>
      </div>
      <div className="relative h-4/5 max-w-3xl overflow-x-auto mt-4 bg-white shadow rounded-lg scrollbar-hide">
        <table className="w-full text-sm text-gray-500">
          <thead className="text-xs text-gray-700 text-left uppercase">
            <tr>
              <th scope="col" className="px-6 py-3">
                Blog Title & Comment
              </th>
              <th scope="col" className="px-6 py-3 max-sm:hidden">
                Date
              </th>
              <th scope="col" className="px-6 py-3">
                Action
              </th>
            </tr>
          </thead>
          <tbody>
            {comments
              .filter((comment) => {
                if (filter === "Approved") return comment.approved === true;
                return comment.approved === false;
              })
              .map((comment, index) => (
                <CommentTableItem
                  key={comment.id}
                  comment={comment}
                  index={index + 1}
                  fetchComments={fetchComments}
                />
              ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Comments;
