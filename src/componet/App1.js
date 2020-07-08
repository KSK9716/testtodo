import React, { Component } from 'react'
import App2 from './App2';
export default class App extends Component {

    constructor() {
        super()
        this.state = { inputValue: '', items: ["task1", "task2", "task3"] }
    }


    change(changeValue) {
        var lastValue = changeValue.target.value
        this.setState({ inputValue: lastValue })

    }
    submit(submitValue) {
        submitValue.preventDefault();
        var sv = this.state.inputValue
        if (sv == '') {
            alert("Please Enter The Value")
        }
        else {
            var newitem = this.state.items.concat(sv)
            this.setState({ inputValue: '', items: newitem })
        }
    }
    delete(deleteValue) {
        var dv = this.state.items.filter((items) => { return items != deleteValue })
        this.setState({ items: dv })
    }
    edit(editValue){
        console.log(editValue)
    }
    render() {

        return (
            <div>
                <h1>ToDo App</h1>

                <form onSubmit={this.submit.bind(this)}>
                    <input id="input" type="text" onChange={this.change.bind(this)} value={this.state.inputValue} />
                    <button ><b>Submit</b></button>
                </form>

                <ul>
                    {this.state.items.map((items, i) => {
                        return <li key={items}>{items}
                            <button onClick={this.delete.bind(this, items)}>Remove</button>
                            <button onClick={this.edit.bind(this,items)}>Edit</button>
                        </li>
                    })}
                </ul>
            </div>
        )
    }
}
