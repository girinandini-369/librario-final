// -------------------------
// CONFIG
// -------------------------
const API_BASE = 'http://localhost:8080';
const CURRENT_MEMBER_ID = 9   // change as needed
const CURRENT_MEMBER_EMAIL = 'anu@example.com';

// -------------------------
// Utility
// -------------------------
function $(sel){ return document.querySelector(sel); }
function el(tag, className, text){
  const e=document.createElement(tag);
  if(className) e.className=className;
  if(text!==undefined) e.textContent=text;
  return e;
}
function showToast(msg,type='info',timeout=3500){
  const root=$('#toastRoot');
  const item=el('div','item '+(type==='success'?'success':type==='error'?'error':''),msg);
  root.appendChild(item);
  setTimeout(()=>{
    item.style.transition='opacity .3s, transform .3s';
    item.style.opacity='0';
    item.style.transform='translateY(8px)';
    setTimeout(()=>item.remove(),400);
  },timeout);
}
function formatCurrency(a){ return '₹'+Number(a).toLocaleString('en-IN',{minimumFractionDigits:2,maximumFractionDigits:2}); }

// -------------------------
// Fetch overdue
// -------------------------
async function fetchOverdue(){
  const c=$('#overdueContainer'); c.innerHTML='';
  c.appendChild(el('div','empty','Loading...'));
  try{
    const res=await fetch(`${API_BASE}/api/transactions/member/${CURRENT_MEMBER_ID}/overdue`);
    if(!res.ok) throw new Error();
    const list=await res.json();
    c.innerHTML='';
    if(!list||list.length===0){ c.appendChild(el('div','empty','No overdue books!')); return;}
    list.forEach(tx=>c.appendChild(createTxnNode(tx)));
  }catch(e){
    c.innerHTML=''; c.appendChild(el('div','empty','Error loading overdue'));
    showToast('Error loading overdue','error');
  }
}
function createTxnNode(tx){
  const node=el('div','txn');
  const book=el('div','book');
  book.appendChild(el('div','title',tx.bookTitle||'Untitled'));
  book.appendChild(el('div','meta','Issued: '+(tx.issueDate?new Date(tx.issueDate).toLocaleDateString():'N/A')+' • Due: '+(tx.dueDate?new Date(tx.dueDate).toLocaleDateString():'N/A')));
  const fine=el('div','fine',formatCurrency(tx.fine??0));
  const actions=el('div','actions');
  const payBtn=el('button','btn-pay','Pay Fine');
  payBtn.onclick=()=>payFine(tx.id,payBtn,node);
  const dummy=el('button','btn-muted','Dummy');
  dummy.onclick=()=>payFine(tx.id,payBtn,node);
  actions.append(payBtn,dummy);
  node.append(book,fine,actions);
  return node;
}

// -------------------------
// Fetch payments
// -------------------------
async function fetchPayments(){
  const listEl=$('#paymentsList');
  listEl.innerHTML=''; listEl.appendChild(el('div','empty','Loading...'));
  try{
    const res=await fetch(`${API_BASE}/api/payments/member/${CURRENT_MEMBER_ID}`);
    if(!res.ok) throw new Error();
    const arr=await res.json();
    listEl.innerHTML='';
    if(!arr||arr.length===0){ listEl.appendChild(el('div','empty','No payments yet.')); return;}
    arr.forEach(p=>{
      const row=el('div','pay-row');
      const left=el('div',null,(p.memberName||'You')+' • '+(p.status||'UNKNOWN'));
      left.appendChild(el('div','muted',p.orderId?'Order: '+p.orderId:'Receipt: '+(p.paymentId||'—')));
      const right=el('div',null,formatCurrency(p.amount));
      row.append(left,right);
      listEl.appendChild(row);
    });
  }catch(e){
    listEl.innerHTML=''; listEl.appendChild(el('div','empty','Error loading payments'));
  }
}

// -------------------------
// Actions
// -------------------------
async function payFine(transactionId,btn,node){
  try{
    btn.disabled=true; btn.textContent='Processing...';
    const res=await fetch(`${API_BASE}/api/payments/dummy-pay`,{
      method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({transactionId})
    });
    const data=await res.json();
    if(!res.ok) throw new Error(data.message);
    showToast('Fine paid ✅','success');
    node.remove(); await fetchPayments();
  }catch(e){ showToast('Payment failed','error'); btn.disabled=false; btn.textContent='Pay Fine'; }
}
async function dummyMembershipUpgrade(){
  if(!confirm('Upgrade membership to PREMIUM (dummy)?')) return;
  const btn=$('#dummyUpgradeBtn'); btn.disabled=true; btn.textContent='Processing...';
  try{
    const res=await fetch(`${API_BASE}/api/payments/dummy-membership-pay`,{
      method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({memberId:CURRENT_MEMBER_ID})
    });
    const data=await res.json();
    if(!res.ok) throw new Error(data.message);
    showToast('Membership upgraded ✅','success');
    $('#membershipType').textContent='PREMIUM';
    await fetchPayments();
  }catch(e){ showToast('Upgrade failed','error'); }
  finally{ btn.disabled=false; btn.textContent='Dummy Upgrade (test)'; }
}

// -------------------------
// Init
// -------------------------
document.addEventListener('DOMContentLoaded',async()=>{
  $('#member-email').textContent=CURRENT_MEMBER_EMAIL;
  $('#memberName').textContent='Member #'+CURRENT_MEMBER_ID;
  $('#memberIdText').textContent=CURRENT_MEMBER_ID;
  $('#avatar').textContent=String(CURRENT_MEMBER_ID).slice(-2);

  $('#dummyUpgradeBtn').onclick=dummyMembershipUpgrade;
  $('#upgradeBtn').onclick=dummyMembershipUpgrade;

  await fetchOverdue();
  await fetchPayments();
});
