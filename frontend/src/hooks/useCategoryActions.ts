import { useCallback } from "react"
import { categoriesApi } from "../api/categories";
import axios from "axios";

export function useCategoryActions(refetchCategories: () => Promise<void>){
    const createCategory = useCallback(async (name: string) => {
        try{
            await categoriesApi.add({ name });

            console.log("successfully added a new category");
            await refetchCategories();
            
        } catch(err) {
            throw axios.isAxiosError(err)
            ? new Error(err.response?.data.error ?? "Failed to create category")
            : err;
        }
    }, [refetchCategories]);

    const updateCategory = useCallback(async (name: string, id: string) => {
        try {
            await categoriesApi.update({ name }, id);
            console.log("successfully updated a category");
            await refetchCategories();

        } catch(err){
            throw axios.isAxiosError(err)
            ? new Error(err.response?.data.error ?? "Failed to update category")
            : err;
        }

    }, [refetchCategories])

    const deleteCategory = useCallback(async (id: string) => {
        try {
            await categoriesApi.delete(id);
            await refetchCategories();
        } catch(err){
            throw axios.isAxiosError(err)
            ? new Error(err.response?.data.error ?? "Failed to delete category")
            : err;
        }
    }, [refetchCategories]);

    return {createCategory, updateCategory, deleteCategory};
}