import "../styles/App.css";
import {
  SignedIn,
  SignedOut,
  SignInButton,
  SignOutButton,
  UserButton,
} from "@clerk/clerk-react";

import Pantry from "./Pantry";
import ReceiptScanner from "./ReceiptScanner"; 
import RecipeMaker from "./Recipes"; 
import { useState } from "react";

interface Ingredient {
  name: string;
  quantity: string;
  expiration: string;
}

function App() {
  const [currentPage, setCurrentPage] = useState("pantry");
  const [selectedIngredients, setSelectedIngredients] = useState<string[]>([]);

  const renderPage = () => {
    switch (currentPage) {
      case "pantry":
        return  <Pantry
        selectedIngredients={selectedIngredients}
        setSelectedIngredients={setSelectedIngredients}
      />;
      case "receipt":
        return <ReceiptScanner />;
      case "recipes":
        return <RecipeMaker selectedIngredients={selectedIngredients} />;
      default:
        return  <Pantry
        selectedIngredients={selectedIngredients}
        setSelectedIngredients={setSelectedIngredients}
      />;
    }
  };

  return (
    <div className="App">
      <SignedOut>
        <div
    style={{
      height: "100vh",
      display: "flex",
      flexDirection: "column",
      justifyContent: "center",
      alignItems: "center",
      textAlign: "center",
      backgroundColor: "#f5f5f5",
    }}
  >
    <h1 style={{ 
        fontSize: "2.5rem", 
        marginBottom: "20px", 
        color: "#333" 
        }}>
      Welcome to ShelfChef
    </h1>
      <p style={{ 
        fontSize: "1.2rem", 
        maxWidth: "500px",
        color: "#555", 
        marginBottom: "30px" }}>
      ShelfChef helps you find recipes based on what's already in your pantry.
      Just add your ingredients and discover what you can cook!
    </p>
    <SignInButton mode="modal">
      <button
        style={{
          padding: "12px 24px",
          fontSize: "18px",
          backgroundColor: "#4285f4",
          color: "white",
          borderRadius: "6px",
          cursor: "pointer",
        }}
      >
        Sign In
      </button>
    </SignInButton>
  </div>
      </SignedOut>

      <SignedIn>
        <div style={{ display: "flex", flexDirection: "column" }}>
          {/* Row 1: Sign-out and user info */}
          <div
            style={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
              padding: "10px",
              gap: "10px",
            }}
          >
            <SignOutButton />
            <UserButton />
          </div>

          {/* Row 2: Page navigation buttons */}
          <div
            style={{
              display: "flex",
              flexDirection: "row",
              justifyContent: "center",
              alignItems: "center",
              paddingBottom: "10px",
              gap: "10px",
            }}
          >
            <button
              onClick={() => setCurrentPage("pantry")}
              style={{
                padding: "8px 16px",
                fontSize: "16px",
                backgroundColor: currentPage === "pantry" ? "#d3e5ff" : "white",
                border: "1px solid #ccc",
                borderRadius: "4px",
              }}
            >
              Pantry
            </button>
            <button
              onClick={() => setCurrentPage("receipt")}
              style={{
                padding: "8px 16px",
                fontSize: "16px",
                backgroundColor: currentPage === "receipt" ? "#d3e5ff" : "white",
                border: "1px solid #ccc",
                borderRadius: "4px",
              }}
            >
              Receipt Scanner
            </button>
            <button
              onClick={() => setCurrentPage("recipes")}
              style={{
                padding: "8px 16px",
                fontSize: "16px",
                backgroundColor: currentPage === "recipes" ? "#d3e5ff" : "white",
                border: "1px solid #ccc",
                borderRadius: "4px",
              }}
            >
              Recipe Finder
            </button>
          </div>

          {/* Dynamic content */}
          {renderPage()}
        </div>
</SignedIn>

    </div>
  );
}

export default App;
