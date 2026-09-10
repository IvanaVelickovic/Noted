import api from "./client";

export type Category = {
    id: string;
    name: string;
}

export type CategoryDetailed = {
    categoryId: string;
    categoryName: string;
    noteCount: string;
}

export type CreateCategory = {name: string}

export const categoriesApi = {
    getAll : () => 
        api.get<Category[]>("/category/get-all").then((res) => res.data),
    add: (payload: CreateCategory) =>
        api.post("/category/add", payload).then((res) => res.data),
    update: (payload: CreateCategory, id: string) => 
        api.put(`/category/update/${id}`, payload).then((res) => res.data),
    delete: (id: string) => 
        api.delete(`/category/delete/${id}`).then((res) => res.data),
    noteCount: () => 
        api.get<CategoryDetailed[]>("/category/note-count").then((res => res.data)),
}