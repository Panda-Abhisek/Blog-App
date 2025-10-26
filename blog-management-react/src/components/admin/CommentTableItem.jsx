import React from "react";
import { assets } from "../../assets/assets";
import { useAppContext } from "../../context/AppContext";
import toast from "react-hot-toast";

const CommentTableItem = ({ comment, fetchComments }) => {
  const { axios, token } = useAppContext();
  const { id, approved, createdAt } = comment;
  const BlogDate = new Date(createdAt);

  const approveComment = async () => {
    try {
      await axios.put(
        `/api/comments/${id}/approve`,
        {}
      );
      await fetchComments(); // refresh the table
    } catch (error) {
      // console.error("Error approving comment:", error);
      toast.error("Error approving comment:", error);
    }
  };

  const deleteComment = async () => {
    try {
      await axios.delete(`/api/comments/${id}`);
      await fetchComments(); // refresh after deletion
    } catch (error) {
      // console.error("Error deleting comment:", error);
      toast.error("Error deleting comment:", error);
    }
  };

  return (
    <tr className="border-y border-gray-300">
      <td className="px-6 py-4">
        <b className="font-medium text-gray-600">Blog</b> : {comment.blogTitle}{" "}
        <br />
        <br />
        <b className="font-medium text-gray-600">Name</b> : {comment.name}{" "}
        <br />
        <b className="font-medium text-gray-600">Comment</b> : {comment.content}
      </td>
      <td className="px-6 py-4 max-sm:hidden">
        {BlogDate.toLocaleDateString()}
      </td>
      <td className="px-6 py-4">
        <div className="inline-flex items-center gap-4">
          {!approved ? (
            <img
              onClick={approveComment}
              src={assets.tick_icon}
              className="w-5 hover:scale-110 transition-all cursor-pointer"
            />
          ) : (
            <p className="text-xs border border-green-600 bg-green-100 text-green-600 rounded-full px-3 py-1">
              Approved
            </p>
          )}
          <img
            src={assets.bin_icon}
            onClick={deleteComment}
            alt=""
            className="w-5 hover:scale-110 transition-all cursor-pointer"
          />
        </div>
      </td>
    </tr>
  );
};

export default CommentTableItem;
