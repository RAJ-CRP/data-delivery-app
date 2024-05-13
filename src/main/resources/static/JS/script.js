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
    var processStartTime = new Date().getTime();
    var responseDisplay = document.getElementById("response");
    responseDisplay.style.display = "block";
    responseDisplay.innerHTML = "<p style='color: #007bff'>Request Processing...</p>"

    var formData = {
        inputPath: document.getElementById("inputPath").value,
        outputPath: document.getElementById("outputPath").value,
        tableName: document.getElementById("tableName").value
    };

    fetch('api/process', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
    })
        .then(response => response.json())
        .then(data => {
            var processEndTime = new Date().getTime();
            var processDuration = (processEndTime - processStartTime) / 1000;

            if (data && data.status) {
                if (data.status === "success") {
                    var statusMessage = data.message || "Process Completed Successfully.";
                    responseDisplay.innerHTML = `
                        <p style="color: green">SUCCESS : ${statusMessage}</p>
                        <p>Time Taken: <strong>${processDuration} seconds</strong></p>
                    `;
                }
                if (data.status === "error") {
                    var statusMessage = data.message || "Process Terminated.";
                    var errorMessage = "";
                    var errors = data.error || {};

                    for (let key in errors) {
                        errorMessage += `<p>${key} : ${errors[key]}</p>`;
                    }
                    responseDisplay.innerHTML = `
                        <p style="color: red">ERROR : ${statusMessage}</p>
                        <div>${errorMessage}</div>
                    `;
                }
            } else {
                responseDisplay.innerHTML = `<p style="color: red">Something went wrong...!</p>`;
            }
        })
        .catch((error) => {
            responseDisplay.innerHTML = `<p style="color: red">Something went wrong...!</p>`;
            console.error('Error:', error);
        });
}