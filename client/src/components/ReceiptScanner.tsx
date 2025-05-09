// ReceiptScanner.tsx
import { useState } from "react";

export default function ReceiptScanner() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleFileUpload = async () => {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append("file", selectedFile);

    fetch("http://localhost:3600/receipt", {
      method: "POST",
      body: formData,
    })
      .then((response) => response.json())
      .then((jsonData) => {
        console.log("Upload success:", jsonData);
      })
      .catch((error) => {
        console.error("Error scanning receipt:", error);
      });
  };

  return (
    <div className="App" style={{ padding: "20px" }}>
      <h1>Upload Receipt</h1>

      <input type="file" accept=".txt,.csv,.pdf" onChange={handleFileChange} />
      <button
        onClick={handleFileUpload}
        style={{ marginLeft: "10px", padding: "8px 16px", fontSize: "16px" }}
      >
        Upload
      </button>
    </div>
  );
}
