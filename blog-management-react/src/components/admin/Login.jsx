/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import { useAppContext } from "../../context/AppContext";
import toast from "react-hot-toast";

const Login = () => {
  const { axios, setToken, navigate, token, setUser } = useAppContext();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(username, password);
    try {
      const { data } = await axios.post("/api/auth/login", {
        username,
        password,
      });
      console.log(data);

      if (data && data.jwt && data.user) {
        setToken(data.jwt);
        localStorage.setItem("token", data.jwt);
        axios.defaults.headers.common["Authorization"] = `Bearer ${data.jwt}`;

        // set user
        setUser(data.user);
        console.log(data.user);
        console.log(JSON.stringify(data.user));
        
        localStorage.setItem("user", JSON.stringify(data.user));

        toast.success("Login successful!");
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      toast.error(error.message);
    }
  };

  useEffect(() => {
    if (token) {
      navigate("/admin");
    }
  }, [token, navigate]);

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
            className="mt-6 w-full sm:max-w-md text-gray-600"
          >
            <div className="flex flex-col">
              <label>Name</label>
              <input
                onChange={(e) => setUsername(e.target.value)}
                value={username}
                type="text"
                required
                placeholder="Your name"
                className="border-b-2 border-gray-300 p-2 outline-none mb-6"
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
                className="border-b-2 border-gray-300 p-2 outline-none mb-6"
              />
            </div>
            <button
              className="w-full py-3 font-medium bg-primary text-white rounded cursor-pointer hover:bg-primary/90 transition-all"
              type="submit"
            >
              Login
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
