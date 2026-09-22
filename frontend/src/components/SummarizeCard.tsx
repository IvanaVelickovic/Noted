import type { SummaryJob } from "../api/jobs";

type SummarizeCardProps = {
  setShowSummarize: React.Dispatch<React.SetStateAction<boolean>>;
  job: SummaryJob | null;
  loading: boolean;
  error: string | null;
  onRegenerate: () => void;
};

function SummarizeCard({
  setShowSummarize,
  job,
  loading,
  error,
  onRegenerate,
}: SummarizeCardProps) {
  return (
    <div className="bg-input-border/50 h-full flex flex-col w-100 border-l-2 border-l-input-border">
      {/* HEADER */}
      <div className="h-[8.8%] border-b-2 border-b-input-border flex justify-between items-center px-5">
        <div className="font-mono text-button-bg">✦SUMMARY</div>
        <div
          className="font-mono text-xl text-date-notes cursor-pointer"
          onClick={() => setShowSummarize(false)}
        >
          x
        </div>
      </div>

      {/* SUMMARIZATION TEXT */}
      <div className="flex-1 flex-col min-h-0 overflow-y-auto text-[0.92rem] p-5 text-[#5A3F45]">
        <p>{loading ? "Loading..." : job?.result}</p>
        {error && <p className="text-red-500">Error occured: {error}</p>}
        <button
          className="mt-3 text-date-notes text-[0.89rem] font-mono cursor-pointer"
          onClick={onRegenerate}
        >
          ↺ REGENERATE
        </button>
      </div>
    </div>
  );
}

export default SummarizeCard;
