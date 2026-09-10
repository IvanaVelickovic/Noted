import { useState } from "react"

export function useInlineEdit() {
    const [editingId, setEditingId] = useState<string | null>(null);
    const [value, setValue] = useState("");

    const startEditing = (id : string, initialValue: string) => {
        setEditingId(id);
        setValue(initialValue);
    }

    const cancelEditing = () => {
        setEditingId(null);
        setValue("");
    }

    return {editingId, value, setValue, setEditingId, startEditing, cancelEditing}
}