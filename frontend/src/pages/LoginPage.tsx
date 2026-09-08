import AuthCard from "../components/AuthCard";

function LoginPage() {
  return (
    <div className="min-h-screen w-full flex justify-center items-center">
      <AuthCard authType="login"></AuthCard>
    </div>
  );
}

export default LoginPage;
