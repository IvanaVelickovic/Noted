import api from "./client";

export type Category = {
    id: string;
    name: string;
}

export const categoriesApi = {
    getAll : () => 
        api.get<Category[]>("/category/get-all").then((res) => res.data),
}