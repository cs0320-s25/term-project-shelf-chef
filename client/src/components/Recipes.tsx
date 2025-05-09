import { useState } from "react";
import { useUser } from "@clerk/clerk-react";
import { getRecipe } from "../utils/api";
import RecipeCard from "./RecipeCard";

interface RecipeMakerProps {
  selectedIngredients: string[];
}

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

const dietaryOptions = ["vegan", "vegetarian", "glutenFree", "dairyFree", "lowFODMAP"];

const RecipeMaker: React.FC<RecipeMakerProps> = ({ selectedIngredients }) => {
  const { user } = useUser();
  const [dietaryRestrictions, setDietaryRestrictions] = useState<string[]>([]);
  const [recipes, setRecipes] = useState<Recipe[]>([]);


  const handleToggleRestriction = (restriction: string) => {
    setDietaryRestrictions((prev) =>
      prev.includes(restriction)
        ? prev.filter((r) => r !== restriction)
        : [...prev, restriction]
    );
  };

  const handleRecipeSearch = async () => {
    if (!user) return;
    try {
      const result = await getRecipe(user.id, selectedIngredients, dietaryRestrictions);
      console.log("Recipe result:", result);
      setRecipes(result);
    } catch (err) {
      console.error("Error fetching recipe:", err);
    }
  };

  return (
    <div style={{ position: "relative", padding: "20px" }}>
      {/* Dietary Restriction Box */}
      <div
        style={{
          position: "absolute",
          top: "10px",
          right: "10px",
          border: "1px solid #ccc",
          borderRadius: "8px",
          padding: "10px",
          backgroundColor: "#f9f9f9",
        }}
      >
        <h4>Dietary Restrictions</h4>
        {dietaryOptions.map((option) => (
          <label key={option} style={{ display: "block", marginBottom: "6px" }}>
            <input
              type="checkbox"
              checked={dietaryRestrictions.includes(option)}
              onChange={() => handleToggleRestriction(option)}
            />{" "}
            {option}
          </label>
        ))}
      </div>

      {/* Selected Ingredients Display */}
      <h2>Selected Ingredients for Recipe Search</h2>
      <div>
        {selectedIngredients.map((ingredient, index) => (
          <div key={index} style={{ fontSize: "16px", marginBottom: "4px" }}>
            {ingredient}
          </div>
        ))}
      </div>

      {/* Search Button */}
      <button
        onClick={handleRecipeSearch}
        style={{
          marginTop: "20px",
          padding: "8px 16px",
          fontSize: "16px",
          border: "1px solid #ccc",
          borderRadius: "4px",
          cursor: "pointer",
        }}
      >
        Search for Recipe
      </button>

      {recipes.length > 0 && (
        <div style={{ marginTop: "40px" }}>
            <h2>Recipes Found</h2>
            {recipes.map((recipe, index) => (
            <RecipeCard key={index} recipe={recipe} />
            ))}
        </div>
        )}
    </div>
  );
};

export default RecipeMaker;
