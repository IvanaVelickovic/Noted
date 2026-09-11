import { useNavigate } from "react-router-dom";
import { authApi } from "../api/auth";

export function useLogout(){
    const navigate = useNavigate();

    const handleLogout = async () => {
        const refreshToken = localStorage.getItem("noted-refresh-token");
            try{
                if(refreshToken){
                    await authApi.logout({refreshToken});
                    localStorage.removeItem("noted-access-token");
                    localStorage.removeItem("noted-refresh-token");
                    navigate("/");
      
                }
            } catch(err){
                console.log(err);
            }
    }
    return handleLogout;
}