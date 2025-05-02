import { useState } from "react";
import "../styles/App.css";

function App() {
  const [inputValue, setInputValue] = useState("");
  const [ingredients, setIngredients] = useState<string[]>([]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim() !== "") {
      setIngredients([...ingredients, inputValue.trim()]);
      setInputValue(""); // Clear input after submitting
    }
  };

  return (
    <div className="App" style={{ padding: "20px" }}>
      <h1>Ingredients</h1>

      <form onSubmit={handleSubmit} style={{ marginBottom: "20px" }}>
        <input
          type="text"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          placeholder="Enter an ingredient"
          style={{
            padding: "8px",
            fontSize: "16px",
            marginRight: "8px",
            width: "250px",
          }}
        />
        <button type="submit" style={{ padding: "8px 16px", fontSize: "16px" }}>
          Submit
        </button>
      </form>

      <ul>
        {ingredients.map((ingredient, index) => (
          <li key={index} style={{ fontSize: "18px", marginBottom: "6px" }}>
            {ingredient}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
