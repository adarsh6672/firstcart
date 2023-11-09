const cashOnDeliveryRadio = document.getElementById('cashOnDelivery');
    const onlinePayRadio = document.getElementById('onlinePay');
    const placeOrderButton = document.getElementById('placeOrderButton');
    const paymentPlaceOrderButton = document.getElementById('paymentPlaceOrderButton');
    const checkoutForm = document.getElementById('checkoutForm');
    var paymentMethodSection = document.querySelector('.payment-method');

    function updatePaymentMethods() {
    if (walletCheckbox.checked) {
  console.log("checked");
    cashOnDeliveryRadio.disabled = true;
    onlinePayRadio.disabled = false;
  } else {
    cashOnDeliveryRadio.disabled = false;
    onlinePayRadio.disabled = false;
  }
  var walletAmount = /* Your wallet amount logic here */;

  if (walletAmount >= /* Minimum amount for online payment */) {
    paymentMethodSection.style.display = 'none';
  } else {
    paymentMethodSection.style.display = 'block';
  }
  }

    paymentPlaceOrderButton.addEventListener('click', (event) => {
        event.preventDefault();
        paymentStart();
    });

    cashOnDeliveryRadio.addEventListener('change', () => {
        placeOrderButton.style.display = 'block';
        paymentPlaceOrderButton.style.display = 'none';
    });

    onlinePayRadio.addEventListener('change', () => {
        placeOrderButton.style.display = 'none';
        paymentPlaceOrderButton.style.display = 'block';
    });