
export const CATEGORY_COLORS = [
    "#7ECFAB", // green
    "#7E9ECF", // blue
    "#CF7EA8", // pink
    "#CFA87E", // orange
    "#A87ECF", // purple
    "#CF7E7E", // red
];

export function getCategoryColor(categoryId: string, categories: {categoryId: string}[]): string{
    const index = categories.findIndex((c) => c.categoryId === categoryId);
    if(index == -1) return CATEGORY_COLORS[0];
    return CATEGORY_COLORS[index % CATEGORY_COLORS.length];
}