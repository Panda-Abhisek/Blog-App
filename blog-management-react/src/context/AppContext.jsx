import { createContext, useContext, useEffect, useState } from "react";
import axios from "./axiosInstance";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const navigate = useNavigate();

  const [blogs, setBlogs] = useState([]);
  const [input, setInput] = useState("");
  const [user, setUser] = useState(null);

  // Fetch logged-in user (the cookie is sent automatically)
  const fetchCurrentUser = async () => {
    try {
      const { data } = await axios.get("/api/auth/me", { withCredentials: true });
      setUser(data);
    } catch (error){
      setUser(null);
      toast.error("There is no logged-in user - ", error.message)
    }
  };

  // const fetchCsrfToken = async () => {
  //   try {
  //     await axios.get("/api/auth/csrf-token", { withCredentials: true });
  //     // The cookie XSRF-TOKEN is now set in the browser
  //   } catch (error) {
  //     console.error("Failed to fetch CSRF token", error);
  //   }
  // };

  // console.log(user);

  const fetchBlogs = async () => {
    try {
      const { data } = await axios.get("/api/blogs");
      // console.log(data);
      data ? setBlogs(data) : toast.error(data.message);
    } catch (error) {
      toast.error(error.message);
    }
  };

  useEffect(() => {
    // fetchCsrfToken()
    fetchCurrentUser();
    fetchBlogs();
  }, []);

  const logout = async () => {
    try {
      await axios.post("/api/auth/logout");
      setUser(null);
      toast.success("Logged out successfully");
      navigate("/");
    } catch {
      toast.error("Logout failed");
    }
  };

  const value = {
    axios,
    navigate,
    blogs,
    setBlogs,
    input,
    setInput,
    user,
    setUser,
    logout,
  };
  // console.log(blogs);

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

export const useAppContext = () => {
  return useContext(AppContext);
};
