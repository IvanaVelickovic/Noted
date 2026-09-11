import { getCategoryColor } from "../utils/categoryColors";
import type { CategoryDetailed } from "../api/categories";

type CategoriesListProps = {
  categories: CategoryDetailed[];
  selectedCategoryId: string | undefined;
  onSelectCategory: (id: string | undefined) => void;
  editingId: string | null;
  value: string;
  onValueChange: (value: string) => void;
  onCommitEdit: () => void;
  onCancelEdit: () => void;
  onStartEditing: (id: string, name: string) => void;
  onDeleteCategory: (id: string) => void;
  onCreateCategory: () => void;
};

function CategoriesList({
  categories,
  selectedCategoryId,
  onSelectCategory,
  editingId,
  value,
  onValueChange,
  onCommitEdit,
  onCancelEdit,
  onStartEditing,
  onDeleteCategory,
  onCreateCategory,
}: CategoriesListProps) {
  return (
    <div className="border-t-2 border-t-input-border flex flex-col gap-y-0.5 p-3 max-h-41 h-full">
      <div className="flex justify-between items-center">
        <h3 className="text-date-notes font-mono text-[0.92rem]">CATEGORIES</h3>
        <button
          className="text-paragraph-light text-[1.2rem] mr-1.5 cursor-pointer"
          onClick={onCreateCategory}
        >
          +
        </button>
      </div>

      <ul className="flex flex-col overflow-y-auto cursor-pointer">
        <li
          className={`flex justify-between items-center text-header text-sm font-mono rounded px-3 py-1.5 ${
            selectedCategoryId === undefined ? "bg-note-fill" : ""
          }`}
          onClick={() => onSelectCategory(undefined)}
        >
          <h5>All notes</h5>
        </li>

        {categories.map((category) => {
          const color = getCategoryColor(category.categoryId, categories);
          const isEditing = editingId === category.categoryId;
          const isSelected = selectedCategoryId === category.categoryId;

          return (
            <li
              className={`flex items-center justify-between text-paragraph text-sm font-mono px-3 py-0.5 ${
                isSelected ? "bg-note-fill" : ""
              }`}
              style={{ color }}
              key={category.categoryId}
              onClick={() =>
                !isEditing && onSelectCategory(category.categoryId)
              }
            >
              <div className="flex items-center gap-x-1.5">
                <span className="text-lg py-0">•</span>
                {isEditing ? (
                  <input
                    autoFocus
                    value={value}
                    onChange={(e) => onValueChange(e.target.value)}
                    onBlur={onCommitEdit}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") onCommitEdit();
                      if (e.key === "Escape") onCancelEdit();
                    }}
                    onClick={(e) => e.stopPropagation()} // don't trigger category selection
                    className="bg-transparent border-b border-button-bg outline-none flex-1"
                  />
                ) : (
                  category.categoryName + " (" + category.noteCount + ")"
                )}
              </div>

              {isSelected && !isEditing && (
                <div className="flex gap-x-0.5">
                  <img
                    src="./images/edit_button.png"
                    className="h-[1.56rem]"
                    onClick={(e) => {
                      e.stopPropagation();
                      onStartEditing(
                        category.categoryId,
                        category.categoryName,
                      );
                    }}
                  />
                  <div
                    className="text-[0.94rem] text-button-bg"
                    onClick={(e) => {
                      e.stopPropagation();
                      onDeleteCategory(category.categoryId);
                    }}
                  >
                    x
                  </div>
                </div>
              )}
            </li>
          );
        })}

        {editingId === "__new__" && (
          <li className="flex items-center gap-x-1.5 text-paragraph text-sm font-mono px-3 py-0.5">
            <span
              className="text-lg py-0"
              style={{
                color: getCategoryColor("new", [
                  ...categories,
                  { categoryId: "new" } as CategoryDetailed,
                ]),
              }}
            >
              •
            </span>
            <input
              autoFocus
              value={value}
              onChange={(e) => onValueChange(e.target.value)}
              onBlur={onCommitEdit}
              onKeyDown={(e) => {
                if (e.key === "Enter") onCommitEdit();
                if (e.key === "Escape") onCancelEdit();
              }}
              placeholder="New category"
              className="bg-transparent border-b border-button-bg outline-none flex-1"
            />
          </li>
        )}
      </ul>
    </div>
  );
}

export default CategoriesList;
