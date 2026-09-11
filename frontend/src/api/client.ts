import axios from "axios";
const apiUrl  = import.meta.env.VITE_API_BASE_URL;

const api = axios.create({
    baseURL: apiUrl,
});

api.interceptors.request.use((config) => {
    const accessToken = localStorage.getItem("noted-access-token");

    if(accessToken){
        config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
})

// REFRESH HANDLING

let isRefreshing = false;
let pendingRequests: Array<(token: string) => void> = [];

function onRefreshed(newToken: string) {
    pendingRequests.forEach((callback) => callback(newToken));
    pendingRequests = [];
}

function clearAuthAndRedirect() {
    localStorage.removeItem("noted-access-token");
    localStorage.removeItem("noted-refresh-token");
    window.location.href = "/login";
}

api.interceptors.response.use(
    (response) => response,
    async(error) => {
        const originalRequest = error.config;

        if( //don't retry refresh calls or already-retried requests
            error.response?.status != 401 ||
            originalRequest._retry ||
            originalRequest.url?.includes("/auth/refresh")
        ) {
            return Promise.reject(error);
        }

        const refreshToken = localStorage.getItem("noted-refresh-token");
        if(!refreshToken){
            clearAuthAndRedirect();
            return Promise.reject(error);
        }

        originalRequest._retry = true;

        if(isRefreshing){
            return new Promise((resovle) => {
                pendingRequests.push((newToken : string) => {
                    originalRequest.headers.Authorization = `Bearer ${newToken}`;
                    resovle(api(originalRequest));
                })
            })
        }

        isRefreshing = true;

        try{
            const res = await axios.post(`${apiUrl}/auth/refresh`, { refreshToken });
            const newAccessToken = res.data.accessToken;

            console.log("new token saved:", newAccessToken);
            localStorage.setItem("noted-access-token", newAccessToken);
            console.log("token in storage now:", localStorage.getItem("noted-access-token"));
            
            onRefreshed(newAccessToken);

            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            console.log("retrying original request:", originalRequest.url, originalRequest.headers);
            return api(originalRequest);
        } catch (refreshError){
            pendingRequests = [];
            clearAuthAndRedirect();
            return Promise.reject(refreshError);
        } finally {
            isRefreshing = false;
        }
    }
)

export default api;