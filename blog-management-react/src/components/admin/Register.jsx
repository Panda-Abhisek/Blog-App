/* eslint-disable no-unused-vars */
import React, { useState } from "react";
import { useAppContext } from "../../context/AppContext";
import toast from "react-hot-toast";

const Register = () => {
  const { axios, navigate } = useAppContext();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true)
      const { data } = await axios.post(
        "/api/auth/register",
        { username, email, password },
        { withCredentials: true }
      );
      toast.success("Registration successful!");
      setLoading(false)
      navigate("/admin");
    } catch (error) {
      setLoading(false)
      toast.error(error.response?.data?.message || "Registration failed");
    }
  };

  return (
    <div className="flex items-center justify-center h-screen">
      <div className="w-full max-w-sm p-6 border border-primary/30 shadow-xl shadow-primary/15 rounded-lg">
        <div className="text-center py-6">
          <h1 className="text-3xl font-bold">
            <span className="text-primary">Admin</span> Register
          </h1>
          <p className="font-light">Create an account to access the admin panel</p>
        </div>
        <form onSubmit={handleSubmit} className="mt-6 text-gray-600">
          <div className="flex flex-col mb-6">
            <label>Name</label>
            <input
              onChange={(e) => setUsername(e.target.value)}
              value={username}
              type="text"
              required
              placeholder="Your name"
              className="border-b-2 border-gray-300 p-2 outline-none"
            />
          </div>
          <div className="flex flex-col mb-6">
            <label>Email</label>
            <input
              onChange={(e) => setEmail(e.target.value)}
              value={email}
              type="email"
              required
              placeholder="Your email"
              className="border-b-2 border-gray-300 p-2 outline-none"
            />
          </div>
          <div className="flex flex-col mb-6">
            <label>Password</label>
            <input
              onChange={(e) => setPassword(e.target.value)}
              value={password}
              type="password"
              required
              placeholder="Enter your password"
              className="border-b-2 border-gray-300 p-2 outline-none"
            />
          </div>
          <button
            type="submit"
            className="w-full py-3 bg-primary text-white rounded hover:bg-primary/90 transition-all"
          >
            {loading ? "Registering..." : "Register"}
          </button>
          <div>
            <p className="font-light mt-3">Already have an account?</p>
            <button type="button" onClick={() => navigate("/admin")} className="text-primary hover:underline">Login Here</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;
