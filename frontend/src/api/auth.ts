import api from "./client";

export type LoginPayload = {email : string, password : string};
export type RegisterPayload = {name: string, email : string, password : string};
export type LogoutPayload = {refreshToken: string}

export const authApi = {
    login : (payload: LoginPayload) => 
        api.post("/auth/login", payload).then((res) => res.data),
    register : (payload : RegisterPayload) => 
        api.post("/auth/register", payload).then((res) => res.data),
    logout: (payload : LogoutPayload) =>
        api.post("/auth/logout", payload).then((res) => res.data),
}