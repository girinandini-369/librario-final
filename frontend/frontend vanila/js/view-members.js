document.addEventListener("DOMContentLoaded", () => {
  const tableBody = document.getElementById("members-table-body");
  const searchInput = document.getElementById("searchInput");

  let membersData = []; // store fetched members

  // Fetch members from backend
  fetch("http://localhost:8080/api/members")
    .then(res => {
      if (!res.ok) throw new Error("Failed to fetch members");
      return res.json();
    })
    .then(members => {
      membersData = members;
      renderTable(membersData);
    })
    .catch(err => {
      console.error("Error loading members:", err);
      tableBody.innerHTML = `<tr><td colspan="6">Failed to load members</td></tr>`;
    });

  // Render members
  function renderTable(members) {
    tableBody.innerHTML = "";

    if (members.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="6">No members found</td></tr>`;
      return;
    }

    members.forEach(m => {
      let row = `
        <tr>
          <td>${m.userName}</td>
          <td>${m.userEmail}</td>
          <td>${m.membershipPlanName || "No Plan"}</td>
          <td>${m.status}</td>
          <td>${m.startDate || "-"}</td>
          <td>${m.endDate || "-"}</td>
        </tr>
      `;
      tableBody.innerHTML += row;
    });
  }

  // Search filter
  searchInput.addEventListener("input", () => {
    const term = searchInput.value.toLowerCase();
    const filtered = membersData.filter(m =>
      m.userName.toLowerCase().includes(term) ||
      m.userEmail.toLowerCase().includes(term)
    );
    renderTable(filtered);
  });
});
