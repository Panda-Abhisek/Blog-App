import React, { useState } from "react";
import { Divider } from "@mui/material";
import toast from "react-hot-toast";
import { Link, useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { useAppContext } from "../../context/AppContext";

const ForgotPassword = () => {
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState('');
  const navigate = useNavigate();
  
  const { axios, user } = useAppContext();

  const onPasswordForgotHandler = async (e) => {
    e.preventDefault();
    // console.log(email);
    
    try {
      setLoading(true);

      const formData = new URLSearchParams();
      formData.append("email", email);
      // console.log(formData.toString());
      
      await axios.post("api/auth/public/forgot-password", formData.toString(), {
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
      });

      toast.success("Password reset email sent! Check your inbox.");
    } catch (error) {
      toast.error("Error sending password reset email. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  //if there is token  exist navigate  the user to the home page if he tried to access the login page
  useEffect(() => {
    if (user) navigate("/");
  }, [user, navigate]);

  return (
    <div className="min-h-[calc(100vh-74px)] flex justify-center items-center">
      <form
        onSubmit={onPasswordForgotHandler}
        className="sm:w-[450px] w-[360px]  shadow-custom py-8 sm:px-8 px-4"
      >
        <div>
          <h1 className="font-montserrat text-center font-semibold text-2xl">
            <span className="text-blue-600">Forgot</span> Password?
          </h1>
          <p className="text-slate-600 text-center ">
            Enter your email a Password reset email will sent
          </p>
        </div>
        <Divider className="font-semibold pb-4"></Divider>

        <div className="flex flex-col gap-2 mt-4">
          <input
            label="Email"
            required
            id="email"
            type="email"
            onChange={(e) => setEmail(e.target.value)}
            value={email}
            placeholder="enter your email"
            className="border-2 border-gray-200 p-2 outline-none mb-2"
          ></input>
        </div>
        <button
          disabled={loading}
          className="w-full py-3 font-medium bg-primary text-white rounded-full cursor-pointer hover:bg-primary/90 transition-all mb-2"
          type="text"
        >
          {loading ? <span>Sending...</span> : "Send"}
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

export default ForgotPassword;
