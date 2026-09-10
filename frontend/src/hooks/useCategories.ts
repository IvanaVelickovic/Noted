import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import { categoriesApi, type CategoryDetailed } from "../api/categories";

export function useCategories(){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [categories, setCategories] = useState<CategoryDetailed[]>([]);

    const fetchCategories = useCallback(async () => {
        setLoading(true);
        setError("");

        try{
            const data = await categoriesApi.noteCount();
            setCategories(data);
        } catch(err) {
            setError(axios.isAxiosError(err) ? err.response?.data?.error ?? "Failed to load categories" : "Failed to load categories");
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchCategories();
    }, [fetchCategories]);

    return { categories, loading, error, fetchCategories };
}