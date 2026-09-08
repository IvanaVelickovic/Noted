import axios from "axios";
const apiUrl  = import.meta.env.VITE_API_BASE_URL;

const api = axios.create({
    baseURL: apiUrl,
});

api.interceptors.request.use((config) => {
    const accessToken = sessionStorage.getItem("noted-access-token");

    if(accessToken){
        config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
})

export default api;