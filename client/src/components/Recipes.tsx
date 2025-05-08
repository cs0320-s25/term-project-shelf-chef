import { useEffect, useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { fetchIngredients } from "../utils/api";

interface Ingredient {
    name: string;
    quantity: string;
    expiration: string;
  }
  
  interface RecipeMakerProps {
    selectedIngredients: Ingredient[];
  }
  
  export default function RecipeMaker({ selectedIngredients }: RecipeMakerProps) {
    // Example: log or send to backend
    useEffect(() => {
      console.log("Selected Ingredients:", selectedIngredients);
  
      // TODO: Send to backend
      // fetch("/query-recipes", { method: "POST", body: JSON.stringify(selectedIngredients) })
    }, [selectedIngredients]);
  
    return (
      <div style={{ padding: "20px" }}>
        <h1>Recipe Maker</h1>
        {selectedIngredients.map((ingredient, index) => (
          <p key={index}>
            {ingredient.name} — {ingredient.quantity} — {ingredient.expiration}
          </p>
        ))}
      </div>
    );
  }
  