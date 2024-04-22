(function init() {
    document.getElementById("inputPath").value = localStorage.getItem("inputPath") || "";
    document.getElementById("outputPath").value = localStorage.getItem("outputPath") || "";
    document.getElementById("tableName").value = localStorage.getItem("tableName") || "";

    document.getElementById("inputPath").addEventListener("input", saveToLocalStorage);
    document.getElementById("outputPath").addEventListener("input", saveToLocalStorage);
    document.getElementById("tableName").addEventListener("input", saveToLocalStorage);

    document.getElementById("response").style.display = "none";
})();

function saveToLocalStorage(e) {
    let id = e.target.id;
    localStorage.setItem(id, document.getElementById(id).value);
}

function submitForm() {

    var formData = {
        inputPath: document.getElementById("inputPath").value,
        outputPath: document.getElementById("outputPath").value,
        tableName: document.getElementById("tableName").value
    };

    fetch('/data-delivery/api/processExcel', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
    })
    .then(response => response.json()) 
    .then(data => {
        document.getElementById("response").style.display = "block";
        document.getElementById("message").innerHTML = data.message || "";
        document.getElementById("error").innerHTML = data.error ? JSON.stringify(data.error) : "";
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}