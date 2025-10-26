import axios from "axios";

axios.defaults.baseURL = import.meta.env.VITE_BASE_URL;
axios.defaults.withCredentials = true;

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
  return null;
}

axios.interceptors.request.use((config) => {
  const csrfToken = getCookie("XSRF-TOKEN");
  if (csrfToken) config.headers["X-XSRF-TOKEN"] = csrfToken;
  return config;
});

export default axios;
