import { useState } from "react";
import "../styles/App.css";
import { secureHeapUsed } from "crypto";

function App() {
  const [inputValue, setInputValue] = useState("");
  const [ingredients, setIngredients] = useState<string[]>([]);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim() !== "") {
      setIngredients([...ingredients, inputValue.trim()]);
      setInputValue(""); // Clear input after submitting
    }
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleFileUpload = async () => {
    if(!selectedFile) {
      return;
    }
    fetch(`http://localhost:3232/receipt?file=${selectedFile}`).then((response) => response.json()).then(
      (jsonData) => {
        return jsonData["success"];
      }
    ).catch((error) => {
      console.error("error scanning receipt");
      }
    )
  }
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

      <div style={{ marginBottom: "20px" }}>
        <input type="file" accept=".txt,.csv,.pdf" onChange={handleFileChange} />
        <button
          onClick={handleFileUpload}
          style={{ marginLeft: "10px", padding: "8px 16px", fontSize: "16px" }}
        >
          Upload File
        </button>
      </div>

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
