type DeleteConfirmationModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  noteTitle?: string;
  isDeleting?: boolean;
};

export default function DeleteConfirmationModal({
  isOpen,
  onClose,
  onConfirm,
  noteTitle = "this note",
  isDeleting = false,
}: DeleteConfirmationModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* BACKDROP */}
      <div
        className="fixed inset-0 bg-black/40 backdrop-blur-[2px] transition-opacity"
        onClick={onClose}
      />

      {/* DIALOG*/}
      <div className="relative z-10 w-full max-w-md rounded-2xl bg-[#dfdddb] p-6 shadow-xl border border-input-border">
        <h3 className="font-display text-header text-xl font-semibold">
          Delete Note?
        </h3>

        <p className="mt-2 text-sm text-paragraph font-mono">
          Are you sure you want to delete{" "}
          <span className="font-bold text-header">"{noteTitle}"</span>? This
          action cannot be undone.
        </p>

        {/* ACTION BUTTONS */}
        <div className="mt-6 flex justify-end gap-x-3">
          <button
            type="button"
            onClick={onClose}
            disabled={isDeleting}
            className="rounded-lg px-4 py-2 font-mono text-sm text-paragraph-light hover:bg-[#EAE1D9] transition-colors cursor-pointer disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={onConfirm}
            disabled={isDeleting}
            className="rounded-lg bg-delete px-4 py-2 font-mono text-sm font-medium text-white hover:bg-red-700 active:bg--red-800 transition-colors cursor-pointer disabled:opacity-50"
          >
            {isDeleting ? "Deleting..." : "Delete"}
          </button>
        </div>
      </div>
    </div>
  );
}
