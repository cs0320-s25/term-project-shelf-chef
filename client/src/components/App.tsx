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
        <SignInButton />
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
