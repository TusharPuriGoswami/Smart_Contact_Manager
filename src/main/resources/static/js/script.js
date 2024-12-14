console.log("this is script file")

const toggleSidebar = () => {

    if($('.sidebar').is(":visible")){

        //true
        //sidebar dikh rha h to use band kerna h 

        $(".sidebar").css("display", "none")
        $(".content").css("margin-left","0%")
    }else{
        //false
        //sidebar ager band h to use show kerna h  
        
        $(".sidebar").css("display", "block")
        $(".content").css("margin-left","20%")
    }

};
const search = () => {
    let query = $("#search-input").val();

    if (query == '') {
        $(".search-result").hide();
    } else {
        console.log(query);

        let url = `http://localhost:9999/search/${query}`;

        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                /* console.log(data); */

                let text = `<div class="list-group">`;

                data.forEach((contact) => {
                    text += `<a href='/user/${contact.cid}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
                });

                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            });
    }
};






