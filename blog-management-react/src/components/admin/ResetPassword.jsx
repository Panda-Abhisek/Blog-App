import React, { useState } from "react";
import { useAppContext } from "../../context/AppContext";
import { Link, useSearchParams } from "react-router-dom";
import { Divider } from "@mui/material";
import toast from "react-hot-toast";

const ResetPassword = () => {
  const { axios } = useAppContext();
  const [loading, setLoading] = useState(false);
  const [password, setPassword] = useState("");
  const [searchParams] = useSearchParams();

  const handleResetPassword = async (e) => {
    e.preventDefault();

    const token = searchParams.get("token");

    setLoading(true);
    try {
      const formData = new URLSearchParams();

      formData.append("token", token);
      formData.append("newPassword", password);

      await axios.post("api/auth/public/reset-password", formData.toString(), {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      toast.success("Password reset successful! You can now log in.");
    } catch (error) {
      toast.error("Error resetting password. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-74px)] flex justify-center items-center">
      <form
        onSubmit={handleResetPassword}
        className="sm:w-[450px] w-[360px]  shadow-custom py-8 sm:px-8 px-4"
      >
        <div>
          <h1 className="font-montserrat text-center font-semibold text-2xl">
            <span className="text-blue-600">Update</span> Your Password
          </h1>
          <p className="text-slate-600 text-center">
            Enter your new Password to Update it
          </p>
        </div>

        <Divider className="font-semibold pb-4"></Divider>

        <div className="flex flex-col gap-2 mt-4">
          <input
            className="border-2 border-gray-200 p-2 outline-none mb-2"
            onChange={(e) => setPassword(e.target.value)}
            value={password}
            label="Password"
            required
            id="password"
            type="password"
            placeholder="enter your Password"
          ></input>
        </div>
        <button
          disabled={loading}
          type="submit"
          className="w-full py-3 font-medium bg-primary text-white rounded-full cursor-pointer hover:bg-primary/90 transition-all mb-2"
        >
          {loading ? <span>Reseting...</span> : "Submit"}
        </button>
        <p className=" text-sm text-slate-700 ">
          <Link className=" underline hover:text-black" to="/login">
            Back To Login
          </Link>
        </p>
      </form>
    </div>
  );
};

export default ResetPassword;
