/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { useAppContext } from "../../context/AppContext";
import toast from "react-hot-toast";
import { Link } from "react-router-dom";
import Divider from "@mui/material/Divider";
import { FcGoogle } from "react-icons/fc";

const apiUrl = import.meta.env.VITE_BASE_URL;

const Login = () => {
  const { axios, navigate, setUser } = useAppContext();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const fetchCsrfToken = async () => {
    try {
      await axios.get("/api/auth/csrf-token", { withCredentials: true });
      // The cookie XSRF-TOKEN is now set in the browser
    } catch (error) {
      console.error("Failed to fetch CSRF token", error);
    }
  };

  const googleOAuth = () => {
    window.location.href = `${apiUrl}/oauth2/authorization/google`;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(username, password);
    try {
      const { data } = await axios.post(
        "/api/auth/login",
        {
          username,
          password,
        },
        { withCredentials: true }
      );
      // console.log(data);
      if (data) {
        // set user
        setUser(data);
        // console.log(data);
        // console.log(JSON.stringify(data));
        await fetchCsrfToken(); // <-- fetch CSRF cookie now
        // await fetchCurrentUser(); // optional: get logged-in user
        toast.success("Login successful!");
        setTimeout(() => {
          navigate("/");
        }, 2000);
      } else {
        toast.error("Login Failed!");
      }
    } catch (error) {
      toast.error(error.message);
    }
  };

  useEffect(() => {}, []);

  return (
    <div className="flex items-center justify-center h-screen">
      <div className="w-full max-w-sm p-6 max-md:m-6 border border-primary/30 shadow-xl shadow-primary/15 rounded-lg">
        <div className="flex flex-col items-center justify-center">
          <div className="w-full py-6 text-center">
            <h1 className="text-3xl font-bold">
              <span className="text-primary">Admin</span> Login
            </h1>
            <p className="font-light">
              Enter your credentials to access the admin panel
            </p>
          </div>
          <form
            onSubmit={handleSubmit}
            className="w-full sm:max-w-md text-gray-600"
          >
            <div className="flex flex-col">
              <label>Name</label>
              <input
                onChange={(e) => setUsername(e.target.value)}
                value={username}
                type="text"
                required
                placeholder="Your name"
                className="border-b-2 border-gray-300 p-2 outline-none mb-4"
              />
            </div>
            <div className="flex flex-col">
              <label>Password</label>
              <input
                onChange={(e) => setPassword(e.target.value)}
                value={password}
                type="password"
                required
                placeholder="Enter your password"
                className="border-b-2 border-gray-300 p-2 outline-none mb-4"
              />
            </div>
            <button
              className="w-full py-3 font-medium bg-primary text-white rounded-full cursor-pointer hover:bg-primary/90 transition-all mb-2"
              type="submit"
            >
              Login
            </button>
            <Divider className="font-light">OR</Divider>
            <div className="flex items-center justify-between gap-1 py-2 ">
              <Link
                onClick={googleOAuth}
                className="flex gap-1 items-center justify-center flex-1 border p-2 shadow-sm shadow-blue-200 rounded-full hover:bg-blue-300 transition-all duration-300"
              >
                <span>
                  <FcGoogle className="text-2xl" />
                </span>
                <span className="font-medium sm:text-customText text-sm">
                  Login with Google
                </span>
              </Link>
            </div>
            <div>
              <p className="font-light mt-2">Not Registered?</p>
              <Link to="/register" className="text-primary hover:underline">
                Register Here
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
