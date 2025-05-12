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

//for front end testing purposes
const mockRecipes: Recipe[] = [
    {
      title: "Ginger Chicken Stir-Fry",
      summary:
        "Ginger Chicken Stir-Fry requires roughly <b>45 minutes</b> from start to finish...",
      instructions:
        "<ol><li>In shallow baking dish, combine teriyaki sauce and water...</li></ol>",
      readyInMinutes: "45",
      servings: "6",
      extendedIngredients: [
        "broccoli/cauliflower/carrots",
        "chicken breasts",
        "chicken broth",
        "brown rice",
        "cornstarch",
        "ground ginger",
        "onion",
        "teriyaki sauce",
        "vegetable oil",
        "water",
      ],
      dishTypes: ["lunch", "main course", "main dish", "dinner"],
      vegetarian: false,
      vegan: false,
      glutenFree: true,
      dairyFree: true,
    },
    {
      title: "Korean Extra Crispy Fried Chicken w Sweet Spicy Glaze",
      summary:
        "Korean Extra Crispy Fried Chicken w Sweet Spicy Glaze might be a good recipe...",
      instructions:
        "1. Cut and discard the end tip of each chicken wing. cut remaining pieces...",
      readyInMinutes: "45",
      servings: "6",
      extendedIngredients: [
        "chicken wings",
        "canola oil",
        "garlic",
        "soy sauce",
        "thai chili peppers",
        "ketchup",
        "rice vinegar",
        "brown sugar",
        "sesame oil",
        "honey",
        "cornstarch",
        "water",
        "salt and pepper",
        "sesame seeds",
      ],
      dishTypes: ["lunch", "main course", "main dish", "dinner"],
      vegetarian: false,
      vegan: false,
      glutenFree: true,
      dairyFree: true,
    },
  ];

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
    // console.log("Mocking recipe fetch...");
    // setRecipes(mockRecipes);
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
