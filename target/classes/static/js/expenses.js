//--------------------------------------------------------------------------------------------------------------------//
//                                                 The editor form                                                    //
//--------------------------------------------------------------------------------------------------------------------//

// Preventing the user from going back
if (window.history.replaceState) {
    window.history.replaceState(null, null, window.location.href);
}

// Preparing the editor form
document.addEventListener('DOMContentLoaded', function() {
    let eventType, sectionHeight;
    const editButtons = document.querySelectorAll('.edit-expense')

    // Different styling for desktop and mobile
    if (window.matchMedia("(max-width: 650px)").matches) {
        eventType = "touchend";
        sectionHeight = "366px";
    }
    else {
        eventType = "click";
        sectionHeight = "742px";
    }

    editButtons.forEach(button => {
        button.addEventListener(eventType, function() {
            // Changing the size of the section
            document.querySelector(".main-table-content").style.height = sectionHeight

            // Getting the necessary data for editing
            const id = button.getAttribute('data-id')
            const product = button.getAttribute('data-product')
            const price = button.getAttribute('data-price')
            const quantity = button.getAttribute('data-quantity')
            const store = button.getAttribute('data-store')
            const day = button.getAttribute('data-day')
            const month = button.getAttribute('data-month')
            const wasteful = button.getAttribute('data-wasteful')

            // Updating the input values in the editing form
            document.getElementById('expense-id').value = id
            document.getElementById('product-input').value = product
            document.getElementById('price-input').value = price
            document.getElementById('quantity-input').value = quantity
            document.getElementById('store-input').value = store
            document.getElementById('days_update').value = day
            document.getElementById('months_update').value = month
            document.getElementById('wasteful').checked = wasteful === "1";

            // Setting the action
            const editorForm = document.getElementById('editor-form')
            editorForm.action = '/expenses/update/' + id

            // Setting up the delete button
            document.getElementById('delete-button').addEventListener('click', function() {
                if (confirm('Are you sure you want to delete this expense?')) {
                    window.location.href = '/expenses/delete/' + id
                }
            })

            // Revealing the editor
            const editor = document.getElementById('hide-editor')
            editor.style.display = "block"
            const updateTh = document.getElementById('updateTh')
            updateTh.innerHTML = "Update<br>Add"
        })
    })
})


//--------------------------------------------------------------------------------------------------------------------//
//                                                Total of the day                                                    //
//--------------------------------------------------------------------------------------------------------------------//

// Calculating and adding "Total of The Day" rows in the table
document.addEventListener('DOMContentLoaded', function() {
    const prices = document.querySelectorAll('.price')

    let totalPrice = 0;
    prices.forEach(function(price) {
        if(price.textContent[0] === "T") {
            price.innerHTML = "↑ Total: &#8377; " + totalPrice.toFixed(2)
            totalPrice = 0
        }
        else {
            totalPrice = totalPrice + Number(price.textContent)
            price.innerHTML = "&#8377; " + price.textContent
        }
    })
})


//--------------------------------------------------------------------------------------------------------------------//
//                                                   Table styling                                                    //
//--------------------------------------------------------------------------------------------------------------------//

// This is for the styling the table
document.addEventListener('DOMContentLoaded', function() {
    const tableRows = document.querySelectorAll('.coloring')

    let index = 0;
    tableRows.forEach(function(tr) {
        if(tr.getAttribute('data-id') == null) {
            if(index % 2 === 0) {
                tr.style.backgroundColor = "#272635"
            }
            else {
                tr.style.backgroundColor = "#201F2C"
            }
            index++
        }
        else {
            index--
            tr.style.backgroundColor = "#F4AC32"
        }
    })
})

// This changes the color of 'wasteful' expenses
document.addEventListener('DOMContentLoaded', function() {
    const wastefulExpenses = document.querySelectorAll('.wastefulColor')

    wastefulExpenses.forEach(function(we) {
        if(we.getAttribute('data-wasteful') === "1")
            we.style.color = "#EF767A"
    })
})


//--------------------------------------------------------------------------------------------------------------------//
//                                                  Day/Month input                                                   //
//--------------------------------------------------------------------------------------------------------------------//

// This is for generating the date options (days in a month)
function generateDaysOfTheMonth(months, days) {
    const savedDay = days.value
    days.innerHTML = ""

    const selectedMonth = months.value;
    let daysCount;

    switch(selectedMonth) {
        case "1": case "3": case "5": case "7": case "8": case "10": case "11":
        case "12":
            daysCount = 31
            break

        case "4": case "6":
        case "9":
            daysCount = 30
            break

        case "2":
            daysCount = 29
        break
    }

    for(let i=1; i<=daysCount; i++) {
        let newOption = document.createElement('option')
        newOption.text = i.toString()
        newOption.value = i.toString()
        days.add(newOption)
    }

    days.value = savedDay;
}

// Setting the Event Listeners and the default values (current day and month)
function setEventListeners(monthId, daysId) {
    const months = document.getElementById(monthId)
    const days = document.getElementById(daysId)

    months.addEventListener('change', function() {
        generateDaysOfTheMonth(months, days)
    })

    document.addEventListener('DOMContentLoaded', function() {
        const bodyElement = document.querySelector('body');
        months.value = bodyElement.getAttribute('data-month')

        // Simulating the change event
        const event = new Event('change')
        months.dispatchEvent(event)
        days.value = bodyElement.getAttribute('data-day')
    })

}
setEventListeners('months_manual', 'days_manual')
setEventListeners('months_image', 'days_image')
setEventListeners('months_update', 'days_update')


//--------------------------------------------------------------------------------------------------------------------//
//                                              The total of the month                                                //
//--------------------------------------------------------------------------------------------------------------------//

// Adding options for the Total Of The Month Selector (it is placed in the div where you can see the total)
document.addEventListener('DOMContentLoaded', function() {
    const totals = document.querySelectorAll('.totalMonth')

    let uniqueDates = []
    totals.forEach(function(t) {
        let date = numberToMonth(t.getAttribute('data-month') - 1) + " 20" + t.getAttribute('data-year')

        if(uniqueDates.length === 0)
            uniqueDates.push(date)
        else {
            if(date !== uniqueDates.at(uniqueDates.length - 1))
                uniqueDates.push(date)
        }
    })

    let lastOption = 1;
    const totalMonthSelector = document.getElementById('totalMonthSelector')
    uniqueDates.forEach(function(ud) {
        let newOption = document.createElement('option')
        newOption.text = ud;
        newOption.value = ud

        totalMonthSelector.add(newOption)

        if(lastOption === 1) {
            lastOption = ud
        }
    })

    const bodyElement = document.querySelector('body');
    let currentMonth = bodyElement.getAttribute('data-month');
    currentMonth = numberToMonth(currentMonth - 1) + " 20" + bodyElement.getAttribute('data-year')
    console.log(currentMonth + " - " + lastOption)
    if(lastOption !== currentMonth) {
        let newOption = document.createElement('option')
        newOption.text = currentMonth
        newOption.value = currentMonth

        totalMonthSelector.add(newOption)
    }

})

// Setting the total expenses for the current month
document.addEventListener('DOMContentLoaded', function() {
    const bodyElement = document.querySelector('body');
    let currentMonth = bodyElement.getAttribute('data-month');

    currentMonth = numberToMonth(currentMonth - 1) + " 20" + bodyElement.getAttribute('data-year')

    document.getElementById('totalMonthSelector').value = currentMonth

    calculateTheTotal(currentMonth)
})


//--------------------------------------------------------------------------------------------------------------------//
//                                      Anchors for navigating the section                                            //
//--------------------------------------------------------------------------------------------------------------------//

// Setting the anchors in the section
document.addEventListener('DOMContentLoaded', function() {

    const expenses = document.querySelectorAll('.totalMonth')
    const lastExpense = expenses[expenses.length - 1]
    let previousDate = numberToMonth(lastExpense.getAttribute('data-month') - 1) + "/20" + lastExpense.getAttribute('data-year');
    for(let i=expenses.length-1; i>= 0; i--) {
        let date = numberToMonth(expenses[i].getAttribute('data-month') - 1) + "/20" +  expenses[i].getAttribute('data-year')

        if(date !== previousDate) {
            expenses[i].setAttribute("id", previousDate)
            previousDate = date
        }
    }

    document.querySelector('.main-table-cells').setAttribute("id", previousDate)
})

// Event listener to manage the anchors in the section
document.getElementById('totalMonthSelector').addEventListener('change', function(event) {
    const selectedMonth = event.target.value

    // Scroll to specific expenses
    const removedSpaces = selectedMonth.replace(/ /, "/")
    location.hash = "#" + removedSpaces;
    console.log(removedSpaces)

    calculateTheTotal(selectedMonth)
})


//--------------------------------------------------------------------------------------------------------------------//
//                                               Other functions                                                      //
//--------------------------------------------------------------------------------------------------------------------//

// Function that closes the editor form
function handleBodyClick() {
    const editor = document.getElementById('hide-editor')
    editor.style.display = "none"

    const updateTh = document.getElementById('updateTh')
    updateTh.innerHTML = "Add"

    // Changing the size of the section
    if (window.matchMedia("(max-width: 650px)").matches)
        document.querySelector(".main-table-content").style.height = "425px"
    else
        document.querySelector(".main-table-content").style.height = "870px"
}


// Function for logout
function handleLogout() {
    window.location.replace("http://localhost:8080/account/logout");
}


// Function for turning the number of a month to its specific string
function numberToMonth(index) {
    const monthsOfTheYear = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]

    if(index >=0 && index <= 11)
        return monthsOfTheYear[index]
}

// This function calculates the total expenditure of the month, and the wasteful one too
function calculateTheTotal(selectedMonth) {
    const expenses = document.querySelectorAll('.monthTotal')
    let totalOfTheMonth = 0;
    let totalOfTheMonthWasteful = 0;

    expenses.forEach(function(expense) {
        let expenseMonth = expense.textContent.split(" ")[1] + " 20" + expense.getAttribute('data-year')
        let expensePrice = Number(expense.getAttribute('data-price'))
        let expenseWasteful = expense.getAttribute('data-wasteful')

        if(expenseMonth === selectedMonth) {
            totalOfTheMonth += expensePrice
        }
        if(expenseMonth === selectedMonth && expenseWasteful === "1") {
            totalOfTheMonthWasteful += expensePrice
        }
    })

    const totalOFM = document.getElementById('totalOfTheMonth')
    totalOFM.innerHTML = "'s total expenditure is <span style='color: #89B6A5;'>&#8377;"
                         + totalOfTheMonth.toFixed(2)
                         + "</span>, with <span style='color: #EF767A;'>&#8377;"
                         + totalOfTheMonthWasteful.toFixed(2) + "</span> wasted!"
}
