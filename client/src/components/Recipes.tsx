import { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { fetchIngredients } from "../utils/api";

interface Ingredient {
    name: string;
    quantity: string;
    expiration: string;
  }
  
  interface RecipeMakerProps {
    selectedIngredients: string[];
  }

  function handleRecipeSearch(): void {
      //insert call to backend for recipes
    throw new Error("Function not implemented.");
}
  
  const RecipeMaker: React.FC<RecipeMakerProps> = ({ selectedIngredients }) => {
    return (
      <div>
        <h2>Selected Ingredients for Recipe Search</h2>
        <ul>
          {selectedIngredients.map((ingredient, index) => (
            <div key={index} style={{ fontSize: "16px" }}>
              {ingredient}
            </div>
          ))}
        </ul>

        <button
              onClick={() => handleRecipeSearch()}
              style={{
                padding: "8px 16px",
                fontSize: "16px",
                border: "1px solid #ccc",
                borderRadius: "4px",
              }}
            >
              Search for Recipe
            </button>
      </div>
    
      
    );





  };

  export default RecipeMaker;


