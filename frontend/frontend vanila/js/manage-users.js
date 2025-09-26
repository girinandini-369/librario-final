const API_URL = "http://localhost:8080/api/members";

document.addEventListener("DOMContentLoaded", () => {
  loadMembers();

  const form = document.getElementById("addMemberForm");
  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    await addMember();
  });
});

// Fetch and render all members
async function loadMembers() {
  try {
    const res = await fetch(API_URL);
    const members = await res.json();

    const tbody = document.querySelector("#membersTable tbody");
    tbody.innerHTML = "";

    members.forEach((m, index) => {
      const row = document.createElement("tr");
      row.setAttribute("data-id", m.id);

      row.innerHTML = `
        <td>${index + 1}</td>
        <td class="editable" data-field="name">${m.userName}</td>
        <td class="editable" data-field="email">${m.userEmail}</td>
        <td class="editable" data-field="status">${m.status}</td>
        <td>${m.membershipPlanName}</td>
        <td>${m.startDate || "-"}</td>
        <td>${m.endDate || "-"}</td>
        <td>
          <button class="action-btn action-edit" onclick="enableEdit(${m.id})">✏️</button>
          <button class="action-btn action-delete" onclick="deleteMember(${m.id})">🗑️</button>
        </td>
      `;
      tbody.appendChild(row);
    });
  } catch (err) {
    console.error("Error loading members:", err);
  }
}

// Add a new member
async function addMember() {
  const name = document.getElementById("name").value.trim();
  const email = document.getElementById("email").value.trim();
  const plan = document.getElementById("plan").value.trim();

  const payload = {
    name,
    email,
    membershipPlanName: plan || null,
  };

  try {
    const res = await fetch(`${API_URL}/add`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      alert("✅ Member added successfully!");
      document.getElementById("addMemberForm").reset();
      loadMembers();
    } else {
      const error = await res.json();
      alert(error.message || "❌ Failed to add member");
    }
  } catch (err) {
    console.error("Error adding member:", err);
  }
}

// Enable inline editing
function enableEdit(id) {
  const row = document.querySelector(`tr[data-id="${id}"]`);
  const editableCells = row.querySelectorAll(".editable");

  editableCells.forEach((cell) => {
    const value = cell.textContent;
    const field = cell.getAttribute("data-field");
    cell.innerHTML = `<input type="text" value="${value}" data-field="${field}" />`;
  });

  const actionCell = row.querySelector("td:last-child");
  actionCell.innerHTML = `
    <button class="action-btn" onclick="saveEdit(${id})">💾 Save</button>
    <button class="action-btn" onclick="cancelEdit(${id})">❌ Cancel</button>
  `;
}

// Cancel edit
function cancelEdit(id) {
  loadMembers();
}

// Save edit
async function saveEdit(id) {
  const row = document.querySelector(`tr[data-id="${id}"]`);
  const inputs = row.querySelectorAll("input");

  const payload = {};
  inputs.forEach((input) => {
    payload[input.getAttribute("data-field")] = input.value;
  });

  try {
    const res = await fetch(`${API_URL}/update/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      alert("✅ Member updated successfully!");
      loadMembers();
    } else {
      const error = await res.json();
      alert(error.message || "❌ Failed to update member");
    }
  } catch (err) {
    console.error("Error updating member:", err);
  }
}

// Delete member
async function deleteMember(id) {
  if (!confirm("Are you sure you want to delete this member?")) return;

  try {
    const res = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    if (res.ok) {
      alert("✅ Member deleted");
      loadMembers();
    } else {
      const error = await res.json();
      alert(error.message || "❌ Failed to delete");
    }
  } catch (err) {
    console.error("Error deleting member:", err);
  }
}
