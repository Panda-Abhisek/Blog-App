import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAppContext } from "../../context/AppContext";

const OAuth2RedirectHandler = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { setUser, axios } = useAppContext();

  useEffect(() => {
    // Remove the token from URL for better UX and security
    const params = new URLSearchParams(location.search);
    if (params.has("token")) {
      params.delete("token");
      const cleanPath =
        location.pathname + (params.toString() ? "?" + params.toString() : "");
      window.history.replaceState({}, document.title, cleanPath);
    }

    // Axios automatically parses JSON and throws on HTTP error status
    const fetchUser = async () => {
      try {
        const res = await axios.get("/api/auth/me", { withCredentials: true });
        // console.log(res.data);
        setUser(res.data);
        navigate("/admin");
      } catch (error) {
        setUser(null);
        navigate("/login");
      }
    };

    fetchUser();
  }, [setUser, navigate, location.search]);

  return <div>Checking authentication...</div>;
};

export default OAuth2RedirectHandler;
