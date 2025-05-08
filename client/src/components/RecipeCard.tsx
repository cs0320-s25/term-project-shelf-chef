import React, { useState } from "react";

interface Recipe {
  title: string;
  summary: string;
  instructions: string;
  readyInMinutes: string;
  servings: string;
  extendedIngredients: string[];
  dishTypes: string[];
  vegetarian: boolean;
  vegan: boolean;
  glutenFree: boolean;
  dairyFree: boolean;
}

interface Props {
  recipe: Recipe;
}

const RecipeCard: React.FC<Props> = ({ recipe }) => {
  const [expanded, setExpanded] = useState(false);

  const cleanHTML = (html: string) => {
    const div = document.createElement("div");
    div.innerHTML = html;
    return div.textContent || div.innerText || "";
  };

  return (
    <div
      style={{
        border: "1px solid #ccc",
        borderRadius: "8px",
        padding: "16px",
        margin: "16px 0",
        boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
      }}
    >
      <h2>{recipe.title}</h2>
      <button
        onClick={() => setExpanded(!expanded)}
        style={{
          marginTop: "8px",
          backgroundColor: "#f0f0f0",
          border: "none",
          padding: "8px 12px",
          cursor: "pointer",
          borderRadius: "4px",
        }}
      >
        {expanded ? "Hide Details ▲" : "Show Details ▼"}
      </button>

      {expanded && (
        <div style={{ marginTop: "12px" }}>
          <p><strong>Summary:</strong> {cleanHTML(recipe.summary)}</p>
          <p><strong>Instructions:</strong></p>
          <div dangerouslySetInnerHTML={{ __html: recipe.instructions }} />
          <p><strong>Ready in:</strong> {recipe.readyInMinutes} minutes</p>
          <p><strong>Servings:</strong> {recipe.servings}</p>
          <p><strong>Ingredients:</strong> {recipe.extendedIngredients.join(", ")}</p>
          <p><strong>Dish Types:</strong> {recipe.dishTypes.join(", ")}</p>
          <p><strong>Tags:</strong> 
            {recipe.vegetarian && " Vegetarian"}
            {recipe.vegan && " Vegan"}
            {recipe.glutenFree && " Gluten-Free"}
            {recipe.dairyFree && " Dairy-Free"}
          </p>
        </div>
      )}
    </div>
  );
};

export default RecipeCard;