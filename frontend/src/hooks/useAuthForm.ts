import { useState } from "react";
import { authApi } from "../api/auth";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export function useAuthForm(authType: "login" | "register"){
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
    });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData((prev) => ({ ...prev, [id]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");

        if (authType === "register" && formData.password !== formData.confirmPassword) {
            setError("Passwords don't match");
            return;
        }

        setLoading(true);
        try {
            let data;
            if (authType === "login") {
                data = await authApi.login({ email: formData.email, password: formData.password });
            } else {
                data = await authApi.register({
                    email: formData.email,
                    name: formData.name,
                    password: formData.password,
                });
            }

            if (data?.accessToken) {
                sessionStorage.setItem("noted-access-token", data.accessToken);
            }
            if (data?.refreshToken) {
                sessionStorage.setItem("noted-refresh-token", data.refreshToken);
            }
            navigate("/notes");
        } catch (err) {
            if (axios.isAxiosError(err)) {
                setError(err.response?.data?.message ?? "Something went wrong");
        } else {
            setError("Something went wrong");
        }
        } finally {
            setLoading(false);
        }
    };

    return { formData, handleChange, handleSubmit, error, loading };
}